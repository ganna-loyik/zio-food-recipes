package repo

import io.getquill.*
import zio.{IO, ZIO, ZLayer}
import domain.*

import javax.sql.DataSource

final class IngredientRepositoryLive(ds: DataSource) extends IngredientRepository:
  import DbContext.*

  private val dsLayer = ZLayer(ZIO.succeed(ds))

  def getAll(): IO[RepositoryError, List[Ingredient]] = {
    run(ingredients.sortBy(_.id))
      .mapError(RepositoryError(_))
      .provide(dsLayer)
  }

  def getById(id: IngredientId): IO[RepositoryError, Option[Ingredient]] =
    run(ingredients.filter(_.id == lift(id)).take(1))
      .map(_.headOption)
      .mapError(RepositoryError(_))
      .provide(dsLayer)

  def add(ingredient: Ingredient): IO[RepositoryError, IngredientId] = {
    run(ingredients.insertValue(lift(ingredient)).returningGenerated(_.id))
      .mapError(RepositoryError(_))
      .provide(dsLayer)
  }

  def update(ingredient: Ingredient): IO[RepositoryError, Unit] = {
    run(ingredients.filter(_.id == lift(ingredient.id)).updateValue(lift(ingredient)))
      .mapError(RepositoryError(_))
      .provide(dsLayer)
      .unit
  }

  def delete(id: IngredientId): IO[RepositoryError, Unit] =
    run(ingredients.filter(_.id == lift(id)).delete)
      .mapError(RepositoryError(_))
      .provide(dsLayer)
      .unit

object IngredientRepositoryLive:

  val layer: ZLayer[DataSource, Nothing, IngredientRepository] =
    ZLayer(
      ZIO
        .service[DataSource]
        .map(ds => IngredientRepositoryLive(ds))
    )
