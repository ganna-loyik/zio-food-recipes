package repo

import zio.*
import domain.*
import repo.RepositoryError

trait IngridientRepository:
  def add(ingridient: Ingridient): IO[RepositoryError, IngridientId]

  def update(ingridient: Ingridient): IO[RepositoryError, Unit]

  def delete(id: IngridientId): IO[RepositoryError, Unit]

  def getAll(): IO[RepositoryError, List[Ingridient]]

  def getById(id: IngridientId): IO[RepositoryError, Option[Ingridient]]

object IngridientRepository:
  def add(ingridient: Ingridient): ZIO[IngridientRepository, RepositoryError, IngridientId] =
    ZIO.serviceWithZIO[IngridientRepository](_.add(ingridient))

  def update(ingridient: Ingridient): ZIO[IngridientRepository, RepositoryError, Unit] =
    ZIO.serviceWithZIO[IngridientRepository](_.update(ingridient))

  def delete(id: IngridientId): ZIO[IngridientRepository, RepositoryError, Unit] =
    ZIO.serviceWithZIO[IngridientRepository](_.delete(id))

  def getAll(): ZIO[IngridientRepository, RepositoryError, List[Ingridient]] =
    ZIO.serviceWithZIO[IngridientRepository](_.getAll())

  def getById(id: IngridientId): ZIO[IngridientRepository, RepositoryError, Option[Ingridient]] =
    ZIO.serviceWithZIO[IngridientRepository](_.getById(id))