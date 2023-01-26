package service

import zio.*
import zio.mock.Expectation.*
import zio.stream.*
import zio.test.*
import zio.test.Assertion.*
import domain.*
import service.*
import service.RecipeService.*
import repo.*
object RecipeServiceSpec extends ZIOSpecDefault:

  val exampleRecipe: (Long, String) => Recipe = (id, name) =>
    Recipe(RecipeId(id), name, None, "instructions for cooking", 30, 30, Set(), Map())

  val getRecipeMock: ULayer[RecipeRepository] =
    RecipeRepoMock.GetById(equalTo(RecipeId(123)), value(Some(exampleRecipe(123, "pie")))) ++
      RecipeRepoMock.GetById(equalTo(RecipeId(124)), value(None))

  val getByNonExistingId: ULayer[RecipeRepository] = RecipeRepoMock.GetById(equalTo(RecipeId(124)), value(None))

  val updateSuccesfullMock: ULayer[RecipeRepository] =
    RecipeRepoMock.GetById(equalTo(RecipeId(123)), value(Some(exampleRecipe(123, "pie")))) ++
      RecipeRepoMock.Update(equalTo(exampleRecipe(123, "cookies")))

  override def spec = suite("recipe service test")(
    test("get recipe") {
      for
        found  <- assertZIO(getRecipeById(RecipeId(123)))(isSome(equalTo(exampleRecipe(123, "pie"))))
        mising <- assertZIO(getRecipeById(RecipeId(124)))(isNone)
      yield found && mising
    }.provideLayer(getRecipeMock >>> RecipeServiceLive.layer),
    suite("update recipe")(
      test("non existing recipe") {
        assertZIO(updateRecipe(exampleRecipe(124, "cookies")).exit)(
          fails(equalTo(DomainError.BusinessError("Recipe with ID 124 not found")))
        )
      }.provideLayer(getByNonExistingId >>> RecipeServiceLive.layer),
      test("update succesfull") {
        assertZIO(updateRecipe(exampleRecipe(123, "cookies")))(isUnit)
      }.provideLayer(updateSuccesfullMock >>> RecipeServiceLive.layer)
    )
  )
