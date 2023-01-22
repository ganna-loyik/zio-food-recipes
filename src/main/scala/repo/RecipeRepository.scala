package repo

import zio.*
import domain.*
import repo.RepositoryError

trait RecipeRepository:
  def add(description: String): IO[RepositoryError, Long]

  def delete(id: Long): IO[RepositoryError, Unit]

  def getAll(): IO[RepositoryError, List[Recipe]]

  def getById(id: Long): IO[RepositoryError, Option[Recipe]]

  def update(recipe: Recipe): IO[RepositoryError, Unit]

object RecipeRepository:
  def add(description: String): ZIO[RecipeRepository, RepositoryError, Long] =
    ZIO.serviceWithZIO[RecipeRepository](_.add(description))

  def delete(id: Long): ZIO[RecipeRepository, RepositoryError, Unit] =
    ZIO.serviceWithZIO[RecipeRepository](_.delete(id))

  def getAll(): ZIO[RecipeRepository, RepositoryError, List[Recipe]] =
    ZIO.serviceWithZIO[RecipeRepository](_.getAll())

  def getById(id: Long): ZIO[RecipeRepository, RepositoryError, Option[Recipe]] =
    ZIO.serviceWithZIO[RecipeRepository](_.getById(id))

  def update(recipe: Recipe): ZIO[RecipeRepository, RepositoryError, Unit] =
    ZIO.serviceWithZIO[RecipeRepository](_.update(recipe))
