import io.getquill.context.ZioJdbc.*
import io.getquill.jdbczio.Quill
import zhttp.http.*
import zhttp.service.*
import zhttp.service.server.ServerChannelFactory
import zio.*
import zio.config.*
import zio.stream.*
import io.getquill.autoQuote
import sourcecode.Text.generate
import api.*
import configuration.Configuration.*
import healthcheck.*
import repo.*
import service.*

object Main extends ZIOAppDefault:

  private val dataSourceLayer = Quill.DataSource.fromPrefix("postgres-db")

  private val repoLayer = RecipeRepositoryLive.layer

  private val serviceLayer = RecipeServiceLive.layer

  val routes =
    api.HttpRoutes.app ++
      Healthcheck.routes

  val program =
    for
      config <- getConfig[ServerConfig]
      _      <- Server.start(config.port, routes)
    yield ()

  override val run =
    program.provide(ServerConfig.layer, serviceLayer, repoLayer, dataSourceLayer)
