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
import healthcheck.*
import javax.sql.DataSource
import org.flywaydb.core.Flyway
import repo.*
import service.*

object Main extends ZIOAppDefault:

  private val dataSourceLayer = Quill.DataSource.fromPrefix("postgres-db")

  private val repoLayer = RecipeRepositoryLive.layer

  private val serviceLayer = RecipeServiceLive.layer

  private def migrate(config: DbConfig) = {
    Flyway
      .configure()
      .validateMigrationNaming(true)
      .dataSource(config.url, config.user, config.password)
      .load()
      .migrate()
  }

  val routes =
    api.HttpRoutes.app ++
      Healthcheck.routes

  val program =
    for
      dbConfig <- getConfig[DbConfig]
      _        <- ZIO.attempt(migrate(dbConfig))
      config   <- getConfig[ServerConfig]
      _        <- Server.start(config.port, routes)
    yield ()

  override val run =
    program.provide(ServerConfig.layer, DbConfig.layer, serviceLayer, repoLayer, dataSourceLayer)
