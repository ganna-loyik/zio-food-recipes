package service

import zio.*
import zio.stream.*
import domain.*
import domain.DomainError.BusinessError
import repo.*

final class RecipeServiceLive(repo: RecipeRepository) extends RecipeService:
  def addRecipe(recipe: Recipe): UIO[RecipeId] =
    repo.add(recipe).orDie

  def deleteRecipe(id: RecipeId): UIO[Unit] =
    repo.delete(id).orDie

  def getAllRecipes(filters: Option[RecipeFilters], sorting: RecipeSorting, sortingOrder: SortingOrder): UIO[List[Recipe]] =
    repo.getAll(filters, sorting, sortingOrder).orDie

  def getRecipeById(id: RecipeId): UIO[Option[Recipe]] =
    repo.getById(id).orDie

  def updateRecipe(recipe: Recipe): IO[DomainError, Unit] =
    for
      foundOption <- getRecipeById(recipe.id)
      _           <- ZIO
        .fromOption(foundOption)
        .mapError(_ => BusinessError(s"Recipe with ID ${recipe.id.value} not found"))
        .flatMap(_ => repo.update(recipe).orDie)
    yield ()

object RecipeServiceLive:
  val layer: URLayer[RecipeRepository, RecipeService] =
    ZLayer(ZIO.service[RecipeRepository].map(RecipeServiceLive(_)))
