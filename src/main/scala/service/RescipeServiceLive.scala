package service

import zio.*
import zio.stream.*
import domain.*
import domain.DomainError.BusinessError
import repo.*

final class RecipeServiceLive(repo: RecipeRepository) extends RecipeService:
  def addRecipe(name: String, description: Option[String]): UIO[RecipeId] =
    repo.add(name, description).orDie

  def deleteRecipe(id: RecipeId): UIO[Unit] =
    repo.delete(id).orDie

  def getAllRecipes(): UIO[List[Recipe]] =
    repo.getAll().orDie

  def getRecipeById(id: RecipeId): UIO[Option[Recipe]] =
    repo.getById(id).orDie

  def updateRecipe(id: RecipeId, name: String, description: Option[String]): IO[DomainError, Unit] =
    for
      foundOption <- getRecipeById(id)
      _           <- ZIO
        .fromOption(foundOption)
        .mapError(_ => BusinessError(s"Recipe with ID ${id.value} not found"))
        .flatMap(recipe => repo.update(Recipe(id, name, description)).orDie)
    yield ()

object RecipeServiceLive:
  val layer: URLayer[RecipeRepository, RecipeService] =
    ZLayer(ZIO.service[RecipeRepository].map(RecipeServiceLive(_)))
