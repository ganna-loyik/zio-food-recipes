package service

import zio.*
import domain.{DomainError, Ingredient, IngredientId}

trait IngredientService:
  def addIngredient(ingredient: Ingredient): UIO[IngredientId]

  def updateIngredient(ingredient: Ingredient): IO[DomainError, Unit]

  def deleteIngredient(id: IngredientId): UIO[Unit]

  def getAllIngredients(): UIO[List[Ingredient]]

  def getIngredientById(id: IngredientId): UIO[Option[Ingredient]]

object IngredientService:
  def addIngredient(ingredient: Ingredient): URIO[IngredientService, IngredientId] =
    ZIO.serviceWithZIO[IngredientService](_.addIngredient(ingredient))

  def updateIngredient(ingredient: Ingredient): ZIO[IngredientService, DomainError, Unit] =
    ZIO.serviceWithZIO[IngredientService](_.updateIngredient(ingredient))

  def deleteIngredient(id: IngredientId): URIO[IngredientService, Unit] =
    ZIO.serviceWithZIO[IngredientService](_.deleteIngredient(id))

  def getAllIngredients(): URIO[IngredientService, List[Ingredient]] =
    ZIO.serviceWithZIO[IngredientService](_.getAllIngredients())

  def getIngredientById(id: IngredientId): URIO[IngredientService, Option[Ingredient]] =
    ZIO.serviceWithZIO[IngredientService](_.getIngredientById(id))
