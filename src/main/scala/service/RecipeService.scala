package service

import zio.*
import domain.{DomainError, Recipe, RecipeFilters, RecipeId, RecipeSorting, SortingOrder}

trait RecipeService:
  def addRecipe(recipe: Recipe): UIO[RecipeId]

  def updateRecipe(recipe: Recipe): IO[DomainError, Unit]

  def deleteRecipe(id: RecipeId): UIO[Unit]

  def getAllRecipes(
    filters: Option[RecipeFilters],
    sorting: RecipeSorting,
    sortingOrder: SortingOrder
  ): UIO[List[Recipe]]

  def getRecipeById(id: RecipeId): UIO[Option[Recipe]]

object RecipeService:
  def addRecipe(recipe: Recipe): URIO[RecipeService, RecipeId] =
    ZIO.serviceWithZIO[RecipeService](_.addRecipe(recipe))

  def updateRecipe(recipe: Recipe): ZIO[RecipeService, DomainError, Unit] =
    ZIO.serviceWithZIO[RecipeService](_.updateRecipe(recipe))

  def deleteRecipe(id: RecipeId): URIO[RecipeService, Unit] =
    ZIO.serviceWithZIO[RecipeService](_.deleteRecipe(id))

  def getAllRecipes(
    filters: Option[RecipeFilters],
    sorting: RecipeSorting,
    sortingOrder: SortingOrder
  ): URIO[RecipeService, List[Recipe]] =
    ZIO.serviceWithZIO[RecipeService](_.getAllRecipes(filters, sorting, sortingOrder))

  def getRecipeById(id: RecipeId): URIO[RecipeService, Option[Recipe]] =
    ZIO.serviceWithZIO[RecipeService](_.getRecipeById(id))
