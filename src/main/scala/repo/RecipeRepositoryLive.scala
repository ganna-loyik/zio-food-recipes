package repo

import io.getquill.*
import io.getquill.context.ZioJdbc.*
import io.getquill.context.Context
import io.getquill.context.qzio.ZioJdbcContext
import zio.{IO, ZIO, ZLayer}
import domain.*

import javax.sql.DataSource

final class RecipeRepositoryLive(ds: DataSource, ctx: PostgresZioJdbcContext[PluralizedTableNames])
  extends RecipeRepository:

  private val dsLayer = ZLayer(ZIO.succeed(ds))

  import ctx._

  inline def recipes = quote {
    querySchema[Recipe]("recipes", _.id -> "id", _.name -> "name", _.description -> "description")
  }

  def add(name: String, description: Option[String]): IO[RepositoryError, RecipeId] =
    ctx
      .run(quote(recipes.insert(_.name -> lift(name), _.description -> lift(description)).returningGenerated(_.id)))
      .mapError(RepositoryError(_))
      .provide(dsLayer)

  def delete(id: RecipeId): IO[RepositoryError, Unit] =
    ctx
      .run(quote(recipes.filter(i => i.id == lift(id)).delete))
      .mapError(e => new RepositoryError(e))
      .provide(dsLayer)
      .unit

  def getAll(): IO[RepositoryError, List[Recipe]] =
    ctx
      .run(quote(recipes))
      .provide(dsLayer)
      .mapError(e => new RepositoryError(e))

  def getById(id: RecipeId): IO[RepositoryError, Option[Recipe]] =
    ctx
      .run(quote(recipes.filter(_.id == lift(id))))
      .map(_.headOption)
      .mapError(e => new RepositoryError(e))
      .provide(dsLayer)

  def update(recipe: Recipe): IO[RepositoryError, Unit] =
    ctx
      .run(quote {
        recipes
          .filter(i => i.id == lift(recipe.id))
          .update(_.name -> lift(recipe.name), _.description -> lift(recipe.description))
      })
      .mapError(e => new RepositoryError(e))
      .provide(dsLayer)
      .unit

object RecipeRepositoryLive:

  val layer: ZLayer[DataSource, Nothing, RecipeRepository] =
    ZLayer(
      ZIO
        .service[DataSource]
        .map(ds => RecipeRepositoryLive(ds, new PostgresZioJdbcContext(PluralizedTableNames)))
    )
