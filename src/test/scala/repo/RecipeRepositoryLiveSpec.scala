package repo

import zio.test.*
import zio.test.Assertion.*
import zio.test.TestAspect.*
import zio.*
import repo.postgresql.*
import domain.*

object PostgresRunnableSpec extends ZIOSpecDefault:

  val containerLayer = ZLayer.scoped(PostgresContainer.make())

  val dataSourceLayer =
    DataSourceBuilderLive.layer.flatMap(builder => ZLayer.fromFunction(() => builder.get.dataSource))

  val repoLayer = RecipeRepositoryLive.layer

  override def spec =
    suite("recipe repository test with postgres test container")(
      test("save recipes returns their ids") {
        for {
          id1 <- RecipeRepository.add("first recipe")
          id2 <- RecipeRepository.add("second recipe")
          id3 <- RecipeRepository.add("third recipe")

        } yield assert(id1)(equalTo(1)) && assert(id2)(equalTo(2)) && assert(id3)(
          equalTo(3)
        )
      },
      test("get all returns 3 recipes") {
        for {
          recipes <- RecipeRepository.getAll()
        } yield assert(recipes)(hasSize(equalTo(3)))
      },
      test("delete first recipe") {
        for {
          _      <- RecipeRepository.delete(1)
          recipe <- RecipeRepository.getById(1)
        } yield assert(recipe)(isNone)
      },
      test("get recipe 2") {
        for {
          recipe <- RecipeRepository.getById(2)
        } yield assert(recipe)(isSome) && assert(recipe.get.description)(equalTo("second recipe"))
      },
      test("update recipe 3") {
        for {
          _      <- RecipeRepository.update(Recipe(3, "updated recipe"))
          recipe <- RecipeRepository.getById(3)
        } yield assert(recipe)(isSome) && assert(recipe.get.description)(equalTo("updated recipe"))
      }
    ).provideShared(containerLayer, dataSourceLayer, repoLayer) @@ sequential
