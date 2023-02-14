package repo

import io.getquill.*
import zio.{IO, ZIO, ZLayer}
import domain.*

import javax.sql.DataSource

final class RecipeTagRepositoryLive(ds: DataSource) extends RecipeTagRepository:
  import DbContext._

  private val dsLayer = ZLayer(ZIO.succeed(ds))

  def getAll(): IO[RepositoryError, List[RecipeTag]] = {
    run(tags.sortBy(_.id))
      .mapError(RepositoryError(_))
      .provide(dsLayer)
  }

  def add(tag: RecipeTag): IO[RepositoryError, RecipeTagId] = {
    run(tags.insertValue(lift(tag)).returningGenerated(_.id))
      .mapError(RepositoryError(_))
      .provide(dsLayer)
  }

  def delete(id: RecipeTagId): IO[RepositoryError, Unit] =
    run(tags.filter(_.id == lift(id)).delete)
      .mapError(RepositoryError(_))
      .provide(dsLayer)
      .unit

object RecipeTagRepositoryLive:

  val layer: ZLayer[DataSource, Nothing, RecipeTagRepository] =
    ZLayer(
      ZIO
        .service[DataSource]
        .map(ds => RecipeTagRepositoryLive(ds))
    )
