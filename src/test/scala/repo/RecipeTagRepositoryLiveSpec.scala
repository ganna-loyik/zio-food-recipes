package repo

import zio.test.*
import zio.test.Assertion.*
import zio.test.TestAspect.*
import zio.*
import repo.postgresql.*
import domain.*

object RecipeTagRepositoryLiveSpec extends ZIOSpecDefault:

  val containerLayer = ZLayer.scoped(PostgresContainer.make())

  val dataSourceLayer =
    DataSourceBuilderLive.layer.flatMap(builder => ZLayer.fromFunction(() => builder.get.dataSource))

  val repoLayer = RecipeTagRepositoryLive.layer

  // some tags are already added to db
  private val initialNum = 3

  override def spec =
    suite("recipe tag repository test with postgres test container")(
      test("save recipe tags returns their ids") {
        for {
          id1 <- RecipeTagRepository.add(RecipeTag(name = "first recipeTag"))
          id2 <- RecipeTagRepository.add(RecipeTag(name = "second recipeTag"))
          id3 <- RecipeTagRepository.add(RecipeTag(name = "third recipeTag"))

        } yield assert(id1.value)(equalTo(initialNum + 1)) &&
        assert(id2.value)(equalTo(initialNum + 2)) &&
        assert(id3.value)(equalTo(initialNum + 3))
      },
      test("error when add tag with duplicated name") {
        for {
          result <- RecipeTagRepository.add(RecipeTag(name = "first recipeTag")).exit
        } yield assert(result.isFailure)(equalTo(true))
      },
      test("get all returns recipe tags") {
        for {
          recipeTags <- RecipeTagRepository.getAll()
        } yield assert(recipeTags)(hasSize(equalTo(initialNum + 3)))
      },
      test("delete recipe tag with id = $initialNum + 1") {
        for {
          _          <- RecipeTagRepository.delete(RecipeTagId(initialNum + 1))
          recipeTags <- RecipeTagRepository.getAll()
        } yield assert(recipeTags.find(_.id == RecipeTagId(initialNum + 1)))(isNone)
      }
    ).provideShared(containerLayer, dataSourceLayer, repoLayer) @@ sequential
