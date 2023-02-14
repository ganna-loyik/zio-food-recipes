package repo

import io.getquill.*
import zio.{IO, ZIO, ZLayer}
import domain.*

import javax.sql.DataSource

final class IngridientRepositoryLive(ds: DataSource) extends IngridientRepository:
  import DbContext._

  private val dsLayer = ZLayer(ZIO.succeed(ds))

  def getAll(): IO[RepositoryError, List[Ingridient]] = {
    run(ingridients.sortBy(_.id))
      .mapError(RepositoryError(_))
      .provide(dsLayer)
  }

  def getById(id: IngridientId): IO[RepositoryError, Option[Ingridient]] =
    run(ingridients.filter(_.id == lift(id)).take(1))
      .map(_.headOption)
      .mapError(RepositoryError(_))
      .provide(dsLayer)

  def add(ingridient: Ingridient): IO[RepositoryError, IngridientId] = {
    run(ingridients.insertValue(lift(ingridient)).returningGenerated(_.id))
      .mapError(RepositoryError(_))
      .provide(dsLayer)
  }

  def update(ingridient: Ingridient): IO[RepositoryError, Unit] = {
    run(ingridients.filter(_.id == lift(ingridient.id)).updateValue(lift(ingridient)))
      .mapError(RepositoryError(_))
      .provide(dsLayer)
      .unit
  }

  def delete(id: IngridientId): IO[RepositoryError, Unit] =
    run(ingridients.filter(_.id == lift(id)).delete)
      .mapError(RepositoryError(_))
      .provide(dsLayer)
      .unit

object IngridientRepositoryLive:

  val layer: ZLayer[DataSource, Nothing, IngridientRepository] =
    ZLayer(
      ZIO
        .service[DataSource]
        .map(ds => IngridientRepositoryLive(ds))
    )
