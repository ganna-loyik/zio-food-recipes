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

  val exampleRecipe = Recipe(123, "apple pie")

  val getRecipeMock: ULayer[RecipeRepository] =
    RecipeRepoMock.GetById(equalTo(123), value(Some(exampleRecipe))) ++
      RecipeRepoMock.GetById(equalTo(124), value(None))

  val getByNonExistingId: ULayer[RecipeRepository] = RecipeRepoMock.GetById(equalTo(124), value(None))

  val updateSuccesfullMock: ULayer[RecipeRepository] =
    RecipeRepoMock.GetById(equalTo(123), value(Some(exampleRecipe))) ++
      RecipeRepoMock.Update(equalTo(exampleRecipe.copy(description = "cookies")))

  def spec = suite("recipe service test")(
    test("get recipe id accept long") {
      for
        found  <- assertZIO(getRecipeById(123))(isSome(equalTo(exampleRecipe)))
        mising <- assertZIO(getRecipeById(124))(isNone)
      yield found && mising
    }.provideLayer(getRecipeMock >>> RecipeServiceLive.layer),
    suite("update recipe")(
      test("non existing recipe") {
        assertZIO(updateRecipe(124, "cookies").exit)(
          fails(equalTo(DomainError.BusinessError("Recipe with ID 124 not found")))
        )
      }.provideLayer(getByNonExistingId >>> RecipeServiceLive.layer),
      test("update succesfull") {
        assertZIO(updateRecipe(123, "cookies"))(isUnit)
      }.provideLayer(updateSuccesfullMock >>> RecipeServiceLive.layer)
    )
  )

  def testLayer: ULayer[RecipeService] =
    ZLayer(for {
      ref   <- Ref.make(Map.empty[String, String])
      queue <- Queue.bounded[Long](10)
    } yield new RecipeService {

      def addRecipe(description: String): UIO[Long] =
        for {
          id <- ZIO.succeed(description.hashCode.abs.toLong)
          _  <- ref.update(m => m + (id.toString -> description))
        } yield id

      def deleteRecipe(id: Long): UIO[Unit] =
        ref.update(map => map.removed(id.toString())) <* queue.offer(id)

      def getAllRecipes(): UIO[List[Recipe]] =
        ref.get
          .map(m =>
            m.view.map { case (key, value) =>
              Recipe(key.toLong, value)
            }.toList
          )

      def getRecipeById(id: Long): UIO[Option[Recipe]] =
        for
          map    <- ref.get
          recipe <- ZIO
            .succeed(map.get(id.toString()))
            .map(op => op.map(des => Recipe(id, des)))
        yield recipe

      def updateRecipe(id: Long, description: String): IO[DomainError, Unit] =
        ref.update(map => map.updated(id.toString(), description))
    })
