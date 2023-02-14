package service

import zio.*
import zio.stream.*
import domain.*
import domain.DomainError.BusinessError
import repo.*

final class IngridientServiceLive(repo: IngridientRepository) extends IngridientService:
  def addIngridient(ingridient: Ingridient): UIO[IngridientId] =
    repo.add(ingridient).orDie

  def deleteIngridient(id: IngridientId): UIO[Unit] =
    repo.delete(id).orDie

  def getAllIngridients(): UIO[List[Ingridient]] =
    repo.getAll().orDie

  def getIngridientById(id: IngridientId): UIO[Option[Ingridient]] =
    repo.getById(id).orDie

  def updateIngridient(ingridient: Ingridient): IO[DomainError, Unit] =
    for
      foundOption <- getIngridientById(ingridient.id)
      _           <- ZIO
        .fromOption(foundOption)
        .mapError(_ => BusinessError(s"Ingridient with ID ${ingridient.id.value} not found"))
        .flatMap(_ => repo.update(ingridient).orDie)
    yield ()

object IngridientServiceLive:
  val layer: URLayer[IngridientRepository, IngridientService] =
    ZLayer(ZIO.service[IngridientRepository].map(IngridientServiceLive(_)))
