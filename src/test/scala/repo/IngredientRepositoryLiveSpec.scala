package repo

import zio.test.*
import zio.test.Assertion.*
import zio.test.TestAspect.*
import zio.*
import repo.postgresql.*
import domain.*

object IngridientRepositoryLiveSpec extends ZIOSpecDefault:

  val containerLayer = ZLayer.scoped(PostgresContainer.make())

  val dataSourceLayer =
    DataSourceBuilderLive.layer.flatMap(builder => ZLayer.fromFunction(() => builder.get.dataSource))

  val repoLayer = IngredientRepositoryLive.layer

  // some ingridients are already added to db
  private val initialNum = 5

  override def spec =
    suite("ingredient repository test with postgres test container")(
      test("save ingredients returns their ids") {
        for {
          id1 <- IngredientRepository.add(Ingredient(name = "first ingredient"))
          id2 <- IngredientRepository.add(Ingredient(name = "second ingredient"))
          id3 <- IngredientRepository.add(Ingredient(name = "third ingredient"))
        } yield assert(id1.value)(equalTo(initialNum + 1)) &&
        assert(id2.value)(equalTo(initialNum + 2)) &&
        assert(id3.value)(equalTo(initialNum + 3))
      },
      test("error when add ingridient with duplicated name") {
        for {
          result <- IngredientRepository.add(Ingredient(name = "first ingredient")).exit
        } yield assert(result.isFailure)(equalTo(true))
      },
      test("get all returns ingredients") {
        for {
          ingredients <- IngredientRepository.getAll()
        } yield assert(ingredients)(hasSize(equalTo(initialNum + 3)))
      },
      test("delete ingredient with id = $initialNum + 1") {
        for {
          _          <- IngredientRepository.delete(IngredientId(initialNum + 1))
          ingredient <- IngredientRepository.getById(IngredientId(initialNum + 1))
        } yield assert(ingredient)(isNone)
      },
      test("get ingredient with id = $initialNum + 2") {
        for {
          ingredient <- IngredientRepository.getById(IngredientId(initialNum + 2))
        } yield assert(ingredient)(isSome) && assert(ingredient.get)(
          equalTo(Ingredient(name = "second ingredient").copy(id = IngredientId(initialNum + 2)))
        )
      },
      test("update ingredient with id = $initialNum + 3") {
        for {
          _          <- IngredientRepository.update(
            Ingredient(name = "updated ingredient").copy(id = IngredientId(initialNum + 3))
          )
          ingredient <- IngredientRepository.getById(IngredientId(initialNum + 3))
        } yield assert(ingredient)(isSome) && assert(ingredient.get.name)(equalTo("updated ingredient"))
      }
    ).provideShared(containerLayer, dataSourceLayer, repoLayer) @@ sequential
