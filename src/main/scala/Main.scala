import io.getquill.context.ZioJdbc.*
import io.getquill.jdbczio.Quill
import zhttp.http.*
import zhttp.service.*
import zhttp.service.server.ServerChannelFactory
import zio.*
import zio.config.*
import zio.stream.*
import api.*
import configuration.Configuration.*
import graphql.schemas.GraphQLSchema
import healthcheck.*
import javax.sql.DataSource
import migration.DatabaseMigrator
import repo.*
import service.*
import caliban.ZHttpAdapter

object Main extends ZIOAppDefault:

  private val dataSourceLayer = Quill.DataSource.fromPrefix("postgres-db")

  val routes =
    api.HttpRoutes.app ++
      Healthcheck.routes

  val program =
    for
      dbConfig <- getConfig[DbConfig]
      _        <- ZIO.attempt(DatabaseMigrator.migrate(dbConfig))

      interpreter <- GraphQLSchema.api.interpreter
      config      <- getConfig[ServerConfig]

      updatedRoutes = routes ++ Http.collectHttp[Request] { case Method.POST -> !! / "graphql" =>
        ZHttpAdapter.makeHttpService(interpreter)
      }
      _            <- Server.start(config.port, updatedRoutes)
    yield ()
      
  override val run =
    program.provide(
      ServerConfig.layer,
      DbConfig.layer,
      RecipeRepositoryLive.layer,
      RecipeServiceLive.layer,
      RecipeTagRepositoryLive.layer,
      RecipeTagServiceLive.layer,
      IngridientRepositoryLive.layer,
      IngridientServiceLive.layer,
      dataSourceLayer
    )
