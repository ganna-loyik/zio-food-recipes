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

  val exampleRecipe = Recipe(RecipeId(123), "apple pie", None)

  val getRecipeMock: ULayer[RecipeRepository] =
    RecipeRepoMock.GetById(equalTo(RecipeId(123)), value(Some(exampleRecipe))) ++
      RecipeRepoMock.GetById(equalTo(RecipeId(124)), value(None))

  val getByNonExistingId: ULayer[RecipeRepository] = RecipeRepoMock.GetById(equalTo(RecipeId(124)), value(None))

  val updateSuccesfullMock: ULayer[RecipeRepository] =
    RecipeRepoMock.GetById(equalTo(RecipeId(123)), value(Some(exampleRecipe))) ++
      RecipeRepoMock.Update(equalTo(exampleRecipe.copy(name = "cookies")))

  override def spec = suite("recipe service test")(
    test("get recipe") {
      for
        found  <- assertZIO(getRecipeById(RecipeId(123)))(isSome(equalTo(exampleRecipe)))
        mising <- assertZIO(getRecipeById(RecipeId(124)))(isNone)
      yield found && mising
    }.provideLayer(getRecipeMock >>> RecipeServiceLive.layer),
    suite("update recipe")(
      test("non existing recipe") {
        assertZIO(updateRecipe(RecipeId(124), "cookies", None).exit)(
          fails(equalTo(DomainError.BusinessError("Recipe with ID 124 not found")))
        )
      }.provideLayer(getByNonExistingId >>> RecipeServiceLive.layer),
      test("update succesfull") {
        assertZIO(updateRecipe(RecipeId(123), "cookies", None))(isUnit)
      }.provideLayer(updateSuccesfullMock >>> RecipeServiceLive.layer)
    )
  )
