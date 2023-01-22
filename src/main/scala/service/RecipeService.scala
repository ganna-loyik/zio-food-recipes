package service

import zio.*
import domain.*

trait RecipeService:
  def addRecipe(description: String): UIO[Long]

  def deleteRecipe(id: Long): UIO[Unit]

  def getAllRecipes(): UIO[List[Recipe]]

  def getRecipeById(id: Long): UIO[Option[Recipe]]

  def updateRecipe(id: Long, description: String): IO[DomainError, Unit]

object RecipeService:
  def addRecipe(description: String): URIO[RecipeService, Long] =
    ZIO.serviceWithZIO[RecipeService](_.addRecipe(description))

  def deleteRecipe(id: Long): URIO[RecipeService, Unit] =
    ZIO.serviceWithZIO[RecipeService](_.deleteRecipe(id))

  def getAllRecipes(): URIO[RecipeService, List[Recipe]] =
    ZIO.serviceWithZIO[RecipeService](_.getAllRecipes())

  def getRecipeById(id: Long): URIO[RecipeService, Option[Recipe]] =
    ZIO.serviceWithZIO[RecipeService](_.getRecipeById(id))

  def updateRecipe(
    id: Long,
    description: String
  ): ZIO[RecipeService, DomainError, Unit] =
    ZIO.serviceWithZIO[RecipeService](_.updateRecipe(id, description))
