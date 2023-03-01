import caliban.ZHttpAdapter
import javax.sql.DataSource
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
import migration.DatabaseMigrator
import persistent.*
import repo.*
import service.*
import subscription.*

import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Props, SpawnProtocol}
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.scaladsl.Behaviors
import akka.util.Timeout
import scala.concurrent.duration._

object Main extends ZIOAppDefault:

  given timeout: Timeout = Timeout(30.seconds)

  given actorSystem: ActorSystem[SpawnProtocol.Command] =
    ActorSystem(Behaviors.setup(_ => SpawnProtocol()), "recipesActorSystem")

  private val dataSourceLayer = Quill.DataSource.fromPrefix("postgres-db")

  val routes: HttpApp[RecipeService & LoginService, Throwable] =
    api.HttpRoutes.app ++
      Healthcheck.routes ++
      AuthRoutes.app

  val program: ZIO[
    DbConfig & ServerConfig & LoginService & RecipeService & RecipeHub & RecipeTagService & JwtDecoder &
      IngridientService,
    Throwable,
    Unit
  ] =
    for
      dbConfig <- getConfig[DbConfig]
      _        <- ZIO.attempt(DatabaseMigrator.migrate(dbConfig))

      interpreter <- GraphQLSchema.api.interpreter
      config      <- getConfig[ServerConfig]

      recipeFormMasterActorRef <- ZIO.fromFuture(_ =>
        actorSystem.ask[ActorRef[RecipeFormEditorCommand]](
          SpawnProtocol.Spawn(RecipeFormMaster(), "recipeFormMaster", Props.empty, _)
        )
      )

      updatedRoutes = routes ++ Http.collectHttp[Request] {
        case Method.POST -> !! / "graphql" => ZHttpAdapter.makeHttpService(interpreter) @@ AuthMiddleware.middleware
        case Method.GET -> !! / "ws" / "graphql" => ZHttpAdapter.makeWebSocketService(interpreter)
      }

      _ <- Server
        .start(config.port, updatedRoutes)
        .provideSomeLayer(ZLayer.succeed(RecipeFormEditorServiceLive(recipeFormMasterActorRef)))
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
