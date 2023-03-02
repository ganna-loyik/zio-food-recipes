package repo

import zio.*
import domain.*
import repo.RepositoryError

trait IngredientRepository:
  def add(ingredient: Ingredient): IO[RepositoryError, IngredientId]

  def update(ingredient: Ingredient): IO[RepositoryError, Unit]

  def delete(id: IngredientId): IO[RepositoryError, Unit]

  def getAll(): IO[RepositoryError, List[Ingredient]]

  def getById(id: IngredientId): IO[RepositoryError, Option[Ingredient]]

object IngredientRepository:
  def add(ingredient: Ingredient): ZIO[IngredientRepository, RepositoryError, IngredientId] =
    ZIO.serviceWithZIO[IngredientRepository](_.add(ingredient))

  def update(ingredient: Ingredient): ZIO[IngredientRepository, RepositoryError, Unit] =
    ZIO.serviceWithZIO[IngredientRepository](_.update(ingredient))

  def delete(id: IngredientId): ZIO[IngredientRepository, RepositoryError, Unit] =
    ZIO.serviceWithZIO[IngredientRepository](_.delete(id))

  def getAll(): ZIO[IngredientRepository, RepositoryError, List[Ingredient]] =
    ZIO.serviceWithZIO[IngredientRepository](_.getAll())

  def getById(id: IngredientId): ZIO[IngredientRepository, RepositoryError, Option[Ingredient]] =
    ZIO.serviceWithZIO[IngredientRepository](_.getById(id))
