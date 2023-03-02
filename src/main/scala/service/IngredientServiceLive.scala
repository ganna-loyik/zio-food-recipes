package service

import zio.*
import zio.stream.*
import domain.*
import domain.DomainError.BusinessError
import repo.*

final class IngredientServiceLive(repo: IngredientRepository) extends IngredientService:
  def addIngredient(ingredient: Ingredient): UIO[IngredientId] =
    repo.add(ingredient).orDie

  def deleteIngredient(id: IngredientId): UIO[Unit] =
    repo.delete(id).orDie

  def getAllIngredients(): UIO[List[Ingredient]] =
    repo.getAll().orDie

  def getIngredientById(id: IngredientId): UIO[Option[Ingredient]] =
    repo.getById(id).orDie

  def updateIngredient(ingredient: Ingredient): IO[DomainError, Unit] =
    for
      foundOption <- getIngredientById(ingredient.id)
      _           <- ZIO
        .fromOption(foundOption)
        .mapError(_ => BusinessError(s"Ingredient with ID ${ingredient.id.value} not found"))
        .flatMap(_ => repo.update(ingredient).orDie)
    yield ()

object IngredientServiceLive:
  val layer: URLayer[IngredientRepository, IngredientService] =
    ZLayer(ZIO.service[IngredientRepository].map(IngredientServiceLive(_)))
