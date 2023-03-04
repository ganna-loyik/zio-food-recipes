package repo

import io.getquill.*
import io.getquill.ast.{AscNullsLast, DescNullsLast}
import zio.{IO, ZIO, ZLayer}

import domain.*

import javax.sql.DataSource
import java.sql.SQLException
import org.postgresql.util.PGobject

final class RecipeRepositoryLive(ds: DataSource) extends RecipeRepository:
  import DbContext.*

  implicit private val encodeIngredientUnit: Encoder[IngredientUnit] = encoder[IngredientUnit](
    java.sql.Types.OTHER,
    (index: Index, value: IngredientUnit, row: PrepareRow) => {
      val pgObj = new PGobject()
      pgObj.setType("ingredient_unit")
      pgObj.setValue(value.toString)
      row.setObject(index, pgObj, java.sql.Types.OTHER)
    }
  )

  implicit private val decodeIngredientUnit: Decoder[IngredientUnit] =
    decoder(row => idx => IngredientUnit.valueOf(row.getObject(idx, classOf[PGobject]).getValue))

  private val dsLayer = ZLayer(ZIO.succeed(ds))

  private def insertRecipeIngredients(recipe: Recipe): ZIO[DataSource, SQLException, Unit] = {
    for {
      filteredIngredients   <- run(ingredients.filter(i => liftQuery(recipe.ingredients.keySet).contains(i.name)))
      ingredientMap          = filteredIngredients.groupBy(_.name).view.mapValues(_.head.id)
      recipe2ingredientsRows = recipe.ingredients.collect {
        case (ingredientName, (amount, unit)) if ingredientMap.contains(ingredientName) =>
          Recipe2IngredientDB(recipe.id, ingredientMap(ingredientName), amount, unit)
      }
      _ <- run(liftQuery(recipe2ingredientsRows).foreach(row => recipe2ingredients.insertValue(row)))
    } yield ()
  }

  private def insertRecipeTags(recipe: Recipe): ZIO[DataSource, SQLException, Unit] = {
    for {
      filteredTags   <- run(tags.filter(t => liftQuery(recipe.tags).contains(t.name)))
      tagMap          = filteredTags.groupBy(_.name).view.mapValues(_.head.id)
      recipe2tagsRows = recipe.tags.flatMap(tag => tagMap.get(tag).map(tagId => Recipe2TagDB(recipe.id, tagId)))
      _              <- run(liftQuery(recipe2tagsRows).foreach(row => recipe2tags.insertValue(row)))
    } yield ()
  }

  def add(recipe: Recipe): IO[RepositoryError, RecipeId] = {
    val action = for {
      recipeId     <- run(recipes.insertValue(lift(RecipeDB.fromRecipe(recipe))).returningGenerated(_.id))
      updatedRecipe = recipe.copy(id = recipeId)
      _            <- insertRecipeIngredients(updatedRecipe)
      _            <- insertRecipeTags(updatedRecipe)
    } yield recipeId

    action
      .mapError(RepositoryError(_))
      .provide(dsLayer)
  }

  def getAll(
    filters: Option[RecipeFilters],
    sorting: RecipeSorting,
    sortingOrder: SortingOrder
  ): IO[RepositoryError, List[Recipe]] = {
    val filteredRecipes = recipesDynamic
      .filterOpt(filters.flatMap(_.name))((row, str) => quote(row.name.toLowerCase.like("%" + str.toLowerCase + "%")))
      .filterOpt(filters.flatMap(_.preparationTimeTo))((row, minutes) => quote(row.preparationTimeMinutes <= minutes))
      .filterOpt(filters.flatMap(_.waitingTimeTo))((row, minutes) => quote(row.waitingTimeMinutes <= minutes))

    val order = sortingOrder match
      case SortingOrder.Ascending  => AscNullsLast
      case SortingOrder.Descending => DescNullsLast

    val sortedRecipes = sorting match
      case RecipeSorting.Name            => filteredRecipes.sortBy(_.name)(Ord(order))
      case RecipeSorting.PreparationTime => filteredRecipes.sortBy(_.preparationTimeMinutes)(Ord(order))
      case _                             => filteredRecipes.sortBy(_.id)(Ord(order))

    val action = for {
      recipeDBs     <- run(sortedRecipes)
      recipeIds      = recipeDBs.map(_.id)
      tagDBs        <- run(
        recipe2tags.filter(row => liftQuery(recipeIds).contains(row.recipeId)).join(tags).on(_.tagId == _.id)
      )
      ingredientDBs <- run(
        recipe2ingredients
          .filter(row => liftQuery(recipeIds).contains(row.recipeId))
          .join(ingredients)
          .on(_.ingredientId == _.id)
      )
    } yield {
      val tagMap = tagDBs.groupBy(_._1.recipeId).view.mapValues(_.map(_._2))
      val ingredientMap = ingredientDBs.groupBy(_._1.recipeId)

      val recipes = recipeDBs.map(recipeDB =>
        recipeDB.toRecipe(tagMap.getOrElse(recipeDB.id, Seq()), ingredientMap.getOrElse(recipeDB.id, Seq()))
      )
      val recipesFilteredByTags =
        if (filters.map(_.tags).getOrElse(Set()).isEmpty) recipes
        else recipes.filter(recipe => filters.get.tags.intersect(recipe.tags).nonEmpty)
      val result =
        if (filters.map(_.ingredients).getOrElse(Set()).isEmpty) recipesFilteredByTags
        else recipes.filter(recipe => filters.get.ingredients.intersect(recipe.ingredients.keySet).nonEmpty)

      result
    }

    action
      .mapError(RepositoryError(_))
      .provide(dsLayer)
  }

  def getById(id: RecipeId): IO[RepositoryError, Option[Recipe]] =
    val action = for {
      recipeDB      <- run(recipes.filter(_.id == lift(id)).take(1))
      ingredientDBs <- run(
        recipe2ingredients.filter(_.recipeId == lift(id)).join(ingredients).on(_.ingredientId == _.id)
      )
      tagDBs        <- run(recipe2tags.filter(_.recipeId == lift(id)).join(tags).on(_.tagId == _.id).map(_._2))
    } yield recipeDB.headOption.map(_.toRecipe(tagDBs, ingredientDBs))

    action
      .mapError(RepositoryError(_))
      .provide(dsLayer)

  def update(recipe: Recipe): IO[RepositoryError, Unit] = {
    val action = for {
      _ <- run(recipes.filter(_.id == lift(recipe.id)).updateValue(lift(RecipeDB.fromRecipe(recipe))))
      _ <- run(recipe2ingredients.filter(_.recipeId == lift(recipe.id)).delete)
      _ <- run(recipe2tags.filter(_.recipeId == lift(recipe.id)).delete)
      _ <- insertRecipeIngredients(recipe)
      _ <- insertRecipeTags(recipe)
    } yield ()

    action
      .mapError(RepositoryError(_))
      .provide(dsLayer)
      .unit
  }

  def delete(id: RecipeId): IO[RepositoryError, Unit] =
    run(quote(recipes.filter(_.id == lift(id)).delete))
      .mapError(e => new RepositoryError(e))
      .provide(dsLayer)
      .unit

object RecipeRepositoryLive:

  val layer: ZLayer[DataSource, Nothing, RecipeRepository] =
    ZLayer(
      ZIO
        .service[DataSource]
        .map(ds => RecipeRepositoryLive(ds))
    )
