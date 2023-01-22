package service

import zio.*
import zio.stream.*
import domain.*
import domain.DomainError.BusinessError
import repo.*

final class RecipeServiceLive(repo: RecipeRepository) extends RecipeService:
  def addRecipe(description: String): UIO[Long] =
    repo.add(description).orDie

  def deleteRecipe(id: Long): UIO[Unit] =
    repo.delete(id).orDie

  def getAllRecipes(): UIO[List[Recipe]] =
    repo.getAll().orDie

  def getRecipeById(id: Long): UIO[Option[Recipe]] =
    repo.getById(id).orDie

  def updateRecipe(id: Long, description: String): IO[DomainError, Unit] =
    for
      foundOption <- getRecipeById(id)
      _           <- ZIO
        .fromOption(foundOption)
        .mapError(_ => BusinessError(s"Recipe with ID ${id} not found"))
        .flatMap(recipe => repo.update(Recipe(id, description)).orDie)
    yield ()

object RecipeServiceLive:
  val layer: URLayer[RecipeRepository, RecipeService] =
    ZLayer(ZIO.service[RecipeRepository].map(RecipeServiceLive(_)))
