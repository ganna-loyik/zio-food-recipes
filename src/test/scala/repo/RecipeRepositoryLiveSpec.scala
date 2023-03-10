package repo

import zio.test.*
import zio.test.Assertion.*
import zio.test.TestAspect.*
import zio.*
import repo.postgresql.*
import domain.*

object RecipeRepositoryLiveSpec extends ZIOSpecDefault:

  val containerLayer = ZLayer.scoped(PostgresContainer.make())

  val dataSourceLayer =
    DataSourceBuilderLive.layer.flatMap(builder => ZLayer.fromFunction(() => builder.get.dataSource))

  val repoLayer = RecipeRepositoryLive.layer

  val exampleRecipe: String => Recipe = name =>
    Recipe(
      name = name,
      description = None,
      instructions = s"instructions for cooking $name",
      preparationTimeMinutes = 30,
      waitingTimeMinutes = 30,
      tags = Set("breakfast"),
      ingredients = Map("apple" -> (100, IngredientUnit.Gram))
    )

  override def spec =
    suite("recipe repository test with postgres test container")(
      test("save recipes returns their ids") {
        for {
          id1 <- RecipeRepository.add(exampleRecipe("first recipe"))
          id2 <- RecipeRepository.add(exampleRecipe("second recipe"))
          id3 <- RecipeRepository.add(exampleRecipe("third recipe"))

        } yield assert(id1.value)(equalTo(1)) && assert(id2.value)(equalTo(2)) && assert(id3.value)(equalTo(3))
      },
      test("skip unexisted tags when add recipe") {
        for {
          id     <- RecipeRepository.add(exampleRecipe("first recipe").copy(tags = Set("unexisted")))
          recipe <- RecipeRepository.getById(id)
          _      <- RecipeRepository.delete(id)
        } yield assert(recipe.get.tags.isEmpty)(equalTo(true))
      },
      test("skip unexisted ingredients when add recipe") {
        for {
          id     <- RecipeRepository
            .add(exampleRecipe("first recipe").copy(ingredients = Map("unexisted" -> (100, IngredientUnit.Gram))))
          recipe <- RecipeRepository.getById(id)
          _      <- RecipeRepository.delete(id)
        } yield assert(recipe.get.ingredients.isEmpty)(equalTo(true))
      },
      test("get all returns 3 recipes") {
        for {
          recipes <- RecipeRepository.getAll(None, RecipeSorting.Name, SortingOrder.Ascending)
        } yield assert(recipes)(hasSize(equalTo(3)))
      },
      test("get all returns 3 recipes sorted by name descending") {
        for {
          recipes <- RecipeRepository.getAll(None, RecipeSorting.Name, SortingOrder.Descending)
        } yield assert(recipes)(equalTo(recipes.sortBy(_.name).reverse))
      },
      test("delete recipe with id = 1") {
        for {
          _      <- RecipeRepository.delete(RecipeId(1))
          recipe <- RecipeRepository.getById(RecipeId(1))
        } yield assert(recipe)(isNone)
      },
      test("get recipe with id = 2") {
        for {
          recipe <- RecipeRepository.getById(RecipeId(2))
        } yield assert(recipe)(isSome) && assert(recipe.get)(
          equalTo(exampleRecipe("second recipe").copy(id = RecipeId(2)))
        )
      },
      test("update recipe with id = 3") {
        for {
          _      <- RecipeRepository.update(exampleRecipe("updated recipe").copy(id = RecipeId(3)))
          recipe <- RecipeRepository.getById(RecipeId(3))
        } yield assert(recipe)(isSome) && assert(recipe.get.name)(equalTo("updated recipe"))
      }
    ).provideShared(containerLayer, dataSourceLayer, repoLayer) @@ sequential
