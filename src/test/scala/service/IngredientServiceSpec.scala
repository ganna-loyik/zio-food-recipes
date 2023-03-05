package service

import zio.*
import zio.mock.Expectation.*
import zio.stream.*
import zio.test.*
import zio.test.Assertion.*
import domain.*
import service.*
import service.IngredientService.*
import repo.*
import repo.mock.*

object IngredientServiceSpec extends ZIOSpecDefault:

  val exampleIngredient: (Long, String) => Ingredient = (id, name) => Ingredient(IngredientId(id), name)

  val getIngredientMock: ULayer[IngredientRepository] =
    IngredientRepoMock.GetById(equalTo(IngredientId(123)), value(Some(exampleIngredient(123, "apple")))) ++
      IngredientRepoMock.GetById(equalTo(IngredientId(124)), value(None))

  val getByNonExistingId: ULayer[IngredientRepository] =
    IngredientRepoMock.GetById(equalTo(IngredientId(124)), value(None))

  val updateSuccesfullMock: ULayer[IngredientRepository] =
    IngredientRepoMock.GetById(equalTo(IngredientId(123)), value(Some(exampleIngredient(123, "apple")))) ++
      IngredientRepoMock.Update(equalTo(exampleIngredient(123, "flour")))

  override def spec = suite("ingredient service test")(
    test("get ingredient") {
      for
        found  <- assertZIO(getIngredientById(IngredientId(123)))(isSome(equalTo(exampleIngredient(123, "apple"))))
        mising <- assertZIO(getIngredientById(IngredientId(124)))(isNone)
      yield found && mising
    }.provideLayer(getIngredientMock >>> IngredientServiceLive.layer),
    suite("update ingredient")(
      test("non existing ingredient") {
        assertZIO(updateIngredient(exampleIngredient(124, "flour")).exit)(
          fails(equalTo(DomainError.BusinessError("Ingredient with ID 124 not found")))
        )
      }.provideLayer(getByNonExistingId >>> IngredientServiceLive.layer),
      test("update succesfull") {
        assertZIO(updateIngredient(exampleIngredient(123, "flour")))(isUnit)
      }.provideLayer(updateSuccesfullMock >>> IngredientServiceLive.layer)
    )
  )
