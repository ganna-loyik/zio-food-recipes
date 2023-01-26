package repo

import zio.*
import domain.*
import repo.RepositoryError

trait RecipeRepository:
  def add(recipe: Recipe): IO[RepositoryError, RecipeId]

  def delete(id: RecipeId): IO[RepositoryError, Unit]

  def getAll(): IO[RepositoryError, List[Recipe]]

  def getById(id: RecipeId): IO[RepositoryError, Option[Recipe]]

  def update(recipe: Recipe): IO[RepositoryError, Unit]

object RecipeRepository:
  def add(recipe: Recipe): ZIO[RecipeRepository, RepositoryError, RecipeId] =
    ZIO.serviceWithZIO[RecipeRepository](_.add(recipe))

  def delete(id: RecipeId): ZIO[RecipeRepository, RepositoryError, Unit] =
    ZIO.serviceWithZIO[RecipeRepository](_.delete(id))

  def getAll(): ZIO[RecipeRepository, RepositoryError, List[Recipe]] =
    ZIO.serviceWithZIO[RecipeRepository](_.getAll())

  def getById(id: RecipeId): ZIO[RecipeRepository, RepositoryError, Option[Recipe]] =
    ZIO.serviceWithZIO[RecipeRepository](_.getById(id))

  def update(recipe: Recipe): ZIO[RecipeRepository, RepositoryError, Unit] =
    ZIO.serviceWithZIO[RecipeRepository](_.update(recipe))
