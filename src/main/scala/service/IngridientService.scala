package service

import zio.*
import domain.{DomainError, Ingridient, IngridientId}

trait IngridientService:
  def addIngridient(ingridient: Ingridient): UIO[IngridientId]

  def updateIngridient(ingridient: Ingridient): IO[DomainError, Unit]

  def deleteIngridient(id: IngridientId): UIO[Unit]

  def getAllIngridients(): UIO[List[Ingridient]]

  def getIngridientById(id: IngridientId): UIO[Option[Ingridient]]

object IngridientService:
  def addIngridient(ingridient: Ingridient): URIO[IngridientService, IngridientId] =
    ZIO.serviceWithZIO[IngridientService](_.addIngridient(ingridient))

  def updateIngridient(ingridient: Ingridient): ZIO[IngridientService, DomainError, Unit] =
    ZIO.serviceWithZIO[IngridientService](_.updateIngridient(ingridient))

  def deleteIngridient(id: IngridientId): URIO[IngridientService, Unit] =
    ZIO.serviceWithZIO[IngridientService](_.deleteIngridient(id))

  def getAllIngridients(): URIO[IngridientService, List[Ingridient]] =
    ZIO.serviceWithZIO[IngridientService](_.getAllIngridients())

  def getIngridientById(id: IngridientId): URIO[IngridientService, Option[Ingridient]] =
    ZIO.serviceWithZIO[IngridientService](_.getIngridientById(id))
