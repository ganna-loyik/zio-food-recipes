package repo

import io.getquill.*
import zio.{IO, ZIO, ZLayer}

import domain.*

import javax.sql.DataSource
import java.sql.SQLException
import org.postgresql.util.PGobject

final class RecipeRepositoryLive(ds: DataSource) extends RecipeRepository:
  import DbContext.*

  implicit private val encodeIngridientUnit: Encoder[IngridientUnit] = encoder[IngridientUnit](
    java.sql.Types.OTHER,
    (index: Index, value: IngridientUnit, row: PrepareRow) => {
      val pgObj = new PGobject()
      pgObj.setType("ingridient_unit")
      pgObj.setValue(value.toString)
      row.setObject(index, pgObj, java.sql.Types.OTHER)
    }
  )

  implicit private val decodeIngridientUnit: Decoder[IngridientUnit] =
    decoder(row => idx => IngridientUnit.valueOf(row.getObject(idx, classOf[PGobject]).getValue))

  private val dsLayer = ZLayer(ZIO.succeed(ds))

  private def insertRecipeIngridients(recipe: Recipe): ZIO[DataSource, SQLException, Unit] = {
    for {
      filteredIngridients   <- run(ingridients.filter(i => liftQuery(recipe.ingridients.keySet).contains(i.name)))
      ingridientMap          = filteredIngridients.groupBy(_.name).view.mapValues(_.head.id)
      recipe2ingridientsRows = recipe.ingridients.collect {
        case (ingridientName, (amount, unit)) if ingridientMap.contains(ingridientName) =>
          Recipe2IngridientDB(recipe.id, ingridientMap(ingridientName), amount, unit)
      }
      _ <- run(liftQuery(recipe2ingridientsRows).foreach(row => recipe2ingridients.insertValue(row)))
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
      _            <- insertRecipeIngridients(updatedRecipe)
      _            <- insertRecipeTags(updatedRecipe)
    } yield recipeId

    action
      .mapError(RepositoryError(_))
      .provide(dsLayer)
  }

  def getAll(): IO[RepositoryError, List[Recipe]] = {
    val action = for {
      recipeDBs     <- run(recipes)
      recipeIds      = recipeDBs.map(_.id)
      tagDBs        <- run(
        recipe2tags.filter(row => liftQuery(recipeIds).contains(row.recipeId)).join(tags).on(_.tagId == _.id)
      )
      ingridientDBs <- run(
        recipe2ingridients
          .filter(row => liftQuery(recipeIds).contains(row.recipeId))
          .join(ingridients)
          .on(_.ingridientId == _.id)
      )
    } yield {
      val tagMap = tagDBs.groupBy(_._1.recipeId).view.mapValues(_.map(_._2))
      val ingridientMap = ingridientDBs.groupBy(_._1.recipeId)
      recipeDBs.map(recipeDB =>
        recipeDB.toRecipe(tagMap.getOrElse(recipeDB.id, Seq()), ingridientMap.getOrElse(recipeDB.id, Seq()))
      )
    }

    action
      .mapError(RepositoryError(_))
      .provide(dsLayer)
  }

  def getById(id: RecipeId): IO[RepositoryError, Option[Recipe]] =
    val action = for {
      recipeDB      <- run(recipes.filter(_.id == lift(id)).take(1))
      ingridientDBs <- run(
        recipe2ingridients.filter(_.recipeId == lift(id)).join(ingridients).on(_.ingridientId == _.id)
      )
      tagDBs        <- run(recipe2tags.filter(_.recipeId == lift(id)).join(tags).on(_.tagId == _.id).map(_._2))
    } yield recipeDB.headOption.map(_.toRecipe(tagDBs, ingridientDBs))

    action
      .mapError(RepositoryError(_))
      .provide(dsLayer)

  def update(recipe: Recipe): IO[RepositoryError, Unit] = {
    val action = for {
      _ <- run(recipes.filter(_.id == lift(recipe.id)).updateValue(lift(RecipeDB.fromRecipe(recipe))))
      _ <- run(recipe2ingridients.filter(_.recipeId == lift(recipe.id)).delete)
      _ <- run(recipe2tags.filter(_.recipeId == lift(recipe.id)).delete)
      _ <- insertRecipeIngridients(recipe)
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
