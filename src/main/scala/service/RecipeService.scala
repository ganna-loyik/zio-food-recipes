package service

import zio.*
import domain.*

trait RecipeService:
  def addRecipe(name: String, description: Option[String]): UIO[RecipeId]

  def deleteRecipe(id: RecipeId): UIO[Unit]

  def getAllRecipes(): UIO[List[Recipe]]

  def getRecipeById(id: RecipeId): UIO[Option[Recipe]]

  def updateRecipe(id: RecipeId, name: String, description: Option[String]): IO[DomainError, Unit]

object RecipeService:
  def addRecipe(name: String, description: Option[String]): URIO[RecipeService, RecipeId] =
    ZIO.serviceWithZIO[RecipeService](_.addRecipe(name, description))

  def deleteRecipe(id: RecipeId): URIO[RecipeService, Unit] =
    ZIO.serviceWithZIO[RecipeService](_.deleteRecipe(id))

  def getAllRecipes(): URIO[RecipeService, List[Recipe]] =
    ZIO.serviceWithZIO[RecipeService](_.getAllRecipes())

  def getRecipeById(id: RecipeId): URIO[RecipeService, Option[Recipe]] =
    ZIO.serviceWithZIO[RecipeService](_.getRecipeById(id))

  def updateRecipe(
    id: RecipeId,
    name: String,
    description: Option[String]
  ): ZIO[RecipeService, DomainError, Unit] =
    ZIO.serviceWithZIO[RecipeService](_.updateRecipe(id, name, description))
