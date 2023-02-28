import io.getquill.context.ZioJdbc.*
import io.getquill.jdbczio.Quill
import zhttp.http.*
import zhttp.service.*
import zhttp.service.server.ServerChannelFactory
import zio.*
import zio.config.*
import zio.stream.*
import api.*
import auth.*
import configuration.Configuration.*
import graphql.schemas.GraphQLSchema
import healthcheck.*
import javax.sql.DataSource
import migration.DatabaseMigrator
import persistent.*
import repo.*
import service.*
import subscription.*
import caliban.ZHttpAdapter
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Props, SpawnProtocol}
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.scaladsl.Behaviors
import akka.NotUsed
import akka.util.Timeout
import scala.concurrent.duration._

object Main extends ZIOAppDefault:

  given timeout: Timeout = Timeout(30.seconds)

  private val dataSourceLayer = Quill.DataSource.fromPrefix("postgres-db")

  private val actorSystemTask: Task[ActorSystem[SpawnProtocol.Command]] =
    ZIO.attempt(ActorSystem(Behaviors.setup(_ => SpawnProtocol()), "recipesActorSystem"))
  private val actorSystemLayer = ZLayer.fromZIO(actorSystemTask)

  val routes: HttpApp[RecipeService & LoginService, Throwable] =
    api.HttpRoutes.app ++
      Healthcheck.routes ++
      AuthRoutes.app

  val program =
    for
      dbConfig <- getConfig[DbConfig]
      _        <- ZIO.attempt(DatabaseMigrator.migrate(dbConfig))

      interpreter <- GraphQLSchema.api.interpreter
      config      <- getConfig[ServerConfig]

      actorSystem <- actorSystemTask
      actorRef    <- ZIO.fromFuture(_ =>
        actorSystem.ask[ActorRef[RecipeFormEditorCommand]](
          SpawnProtocol.Spawn(RecipeFormEditor("1"), "Form1", Props.empty, _)
        )(timeout, actorSystem.scheduler)
      )
      response     <- ZIO.fromFuture(_ => actorRef.ask(ref => UpdateName("test", ref))(timeout, actorSystem.scheduler))
      _           <- Console.print("-" * 100 + "\n" + response)


      updatedRoutes = routes ++ Http.collectHttp[Request] {
        case Method.POST -> !! / "graphql" => ZHttpAdapter.makeHttpService(interpreter) @@ AuthMiddleware.middleware
        case Method.GET -> !! / "ws" / "graphql" => ZHttpAdapter.makeWebSocketService(interpreter)
      } ++ Http.collectZIO { case Method.GET -> !! / "temp" =>
        ZIO.fromFuture(_ => actorRef.ask(ref => UpdateDescription("test", ref))(timeout, actorSystem.scheduler)).map {
          resp => Response.json(resp.toString)
        }
      }

      _ <- Server.start(config.port, updatedRoutes)
    yield ()

  override val run =
    program.provide(
      JwtDecoderLive.layer,
      JwtEncoderLive.layer,
      LoginServiceLive.layer,
      ServerConfig.layer,
      DbConfig.layer,
      RecipeRepositoryLive.layer,
      RecipeServiceLive.layer,
      RecipeHubLive.layer,
      RecipeTagRepositoryLive.layer,
      RecipeTagServiceLive.layer,
      IngridientRepositoryLive.layer,
      IngridientServiceLive.layer,
      dataSourceLayer
    )
