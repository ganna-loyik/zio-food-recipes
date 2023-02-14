package repo

import zio.*
import domain.*
import repo.RepositoryError

trait RecipeTagRepository:
  def add(tag: RecipeTag): IO[RepositoryError, RecipeTagId]

  def delete(id: RecipeTagId): IO[RepositoryError, Unit]

  def getAll(): IO[RepositoryError, List[RecipeTag]]

object RecipeTagRepository:
  def add(tag: RecipeTag): ZIO[RecipeTagRepository, RepositoryError, RecipeTagId] =
    ZIO.serviceWithZIO[RecipeTagRepository](_.add(tag))

  def delete(id: RecipeTagId): ZIO[RecipeTagRepository, RepositoryError, Unit] =
    ZIO.serviceWithZIO[RecipeTagRepository](_.delete(id))

  def getAll(): ZIO[RecipeTagRepository, RepositoryError, List[RecipeTag]] =
    ZIO.serviceWithZIO[RecipeTagRepository](_.getAll())
