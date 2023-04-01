import caliban.ZHttpAdapter
import javax.sql.DataSource
import io.getquill.context.ZioJdbc.*
import io.getquill.jdbczio.Quill
import sttp.tapir.json.zio.*
import zio.*
import zio.config.*
import zio.http.*
import zio.http.model.*
import zio.stream.*

import api.*
import auth.*
import configuration.Configuration.{ServerConfig as CustomServerConfig, *}
import graphql.schemas.GraphQLSchema
import healthcheck.*
import migration.DatabaseMigrator
import persistent.common.*
import persistent.master.*
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

  val routes: App[RecipeService & LoginService] =
    (api.HttpRoutes.app ++
      Healthcheck.routes ++
      AuthRoutes.app).withDefaultErrorResponse

  val program: ZIO[
    DbConfig & CustomServerConfig & LoginService & RecipeService & RecipeHub & RecipeTagService & JwtDecoder &
      IngredientService,
    Throwable,
    Unit
  ] =
    for
      dbConfig <- getConfig[DbConfig]
      _        <- ZIO.attempt(DatabaseMigrator.migrate(dbConfig))

      interpreter <- GraphQLSchema.api.interpreter

      recipeFormMasterActorRef <- ZIO.fromFuture(_ =>
        actorSystem.ask[ActorRef[RecipeFormEditorCommand]](
          SpawnProtocol.Spawn(RecipeFormMaster(), "recipeFormMaster", Props.empty, _)
        )
      )

      calibanRoutes = Http
        .collectRoute[Request] {
          case Method.POST -> !! / "graphql" => ZHttpAdapter.makeHttpService(interpreter) @@ AuthMiddleware.middleware
          case Method.GET -> !! / "ws" / "graphql" => ZHttpAdapter.makeWebSocketService(interpreter)
        }
        .withDefaultErrorResponse

      config <- getConfig[CustomServerConfig]

      port <- Server
        .serve(calibanRoutes ++ routes)
        .provideSomeLayer(ZLayer.succeed(RecipeFormEditorServiceLive(recipeFormMasterActorRef)))
        .provideSomeLayer(Server.defaultWithPort(config.port))
    yield ()

  override val run =
    program.provide(
      JwtDecoderLive.layer,
      JwtEncoderLive.layer,
      LoginServiceLive.layer,
      CustomServerConfig.layer,
      DbConfig.layer,
      RecipeRepositoryLive.layer,
      RecipeServiceLive.layer,
      RecipeHubLive.layer,
      RecipeTagRepositoryLive.layer,
      RecipeTagServiceLive.layer,
      IngredientRepositoryLive.layer,
      IngredientServiceLive.layer,
      dataSourceLayer
    )
