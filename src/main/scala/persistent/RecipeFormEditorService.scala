package persistent

import akka.actor.typed.{ActorRef, ActorSystem, Props, SpawnProtocol}
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import scala.concurrent.duration._
import zio.*

trait RecipeFormEditorService:
  def addRecipeFormEditor(): Task[String]

object RecipeFormEditorService:
  def addRecipeFormEditor(): RIO[RecipeFormEditorService, String] =
    ZIO.serviceWithZIO[RecipeFormEditorService](_.addRecipeFormEditor())

final class RecipeFormEditorServiceLive(recipeFormMaster: ActorRef[RecipeFormEditorCommand])(using
  actorSystem: ActorSystem[_],
  timeout: Timeout
) extends RecipeFormEditorService:

  def addRecipeFormEditor(): Task[String] =
    ZIO
      .fromFuture { _ =>
        recipeFormMaster.ask[RecipeFormEditorResponse](ref => Create(ref))
      }
      .flatMap {
        case CreatedResponse(reply) if reply.isSuccess => ZIO.succeed(reply.getValue)
        case _                                         => ZIO.fail(Throwable("Fail to add recipe form editor"))
      }

object RecipeFormEditorServiceLive:
  def layer()(using
    actorSystem: ActorSystem[SpawnProtocol.Command],
    timeout: Timeout
  ): TaskLayer[RecipeFormEditorService] =
    ZLayer(
      ZIO
        .fromFuture(_ =>
          actorSystem.ask[ActorRef[RecipeFormEditorCommand]](
            SpawnProtocol.Spawn(RecipeFormMaster(), "recipeFormMaster", Props.empty, _)
          )
        )
        .map(actorRef => RecipeFormEditorServiceLive(actorRef))
    )
