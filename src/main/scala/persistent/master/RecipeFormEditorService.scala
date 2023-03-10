package persistent.master

import akka.actor.typed.{ActorRef, ActorSystem, Props, SpawnProtocol}
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import scala.concurrent.duration._
import zio.*
import domain.IngredientUnit
import persistent.common.*

trait RecipeFormEditorService:
  def addRecipeForm(): Task[String]

  def getRecipeForm(id: String): Task[Summary]

  def updateName(id: String, name: String): Task[Unit]

  def updateDescription(id: String, description: String): Task[Unit]

  def updateInstructions(id: String, instructions: String): Task[Unit]

  def updatePreparationTime(id: String, minutes: Int): Task[Unit]

  def updateWaitingTime(id: String, minutes: Int): Task[Unit]

  def addIngredient(id: String, ingredient: String, amount: Int, unit: IngredientUnit): Task[Unit]

  def updateIngredient(id: String, ingredient: String, amount: Int, unit: IngredientUnit): Task[Unit]

  def removeIngredient(id: String, ingredient: String): Task[Unit]

  def addTag(id: String, tag: String): Task[Unit]

  def removeTag(id: String, tag: String): Task[Unit]

  def saveRecipeForm(id: String): Task[Unit]

object RecipeFormEditorService:
  def addRecipeForm(): RIO[RecipeFormEditorService, String] =
    ZIO.serviceWithZIO[RecipeFormEditorService](_.addRecipeForm())

  def getRecipeForm(id: String): RIO[RecipeFormEditorService, Summary] =
    ZIO.serviceWithZIO[RecipeFormEditorService](_.getRecipeForm(id))

  def updateName(id: String, name: String): RIO[RecipeFormEditorService, Unit] =
    ZIO.serviceWithZIO[RecipeFormEditorService](_.updateName(id, name))

  def updateDescription(id: String, description: String): RIO[RecipeFormEditorService, Unit] =
    ZIO.serviceWithZIO[RecipeFormEditorService](_.updateDescription(id, description))

  def updateInstructions(id: String, instructions: String): RIO[RecipeFormEditorService, Unit] =
    ZIO.serviceWithZIO[RecipeFormEditorService](_.updateInstructions(id, instructions))

  def updatePreparationTime(id: String, minutes: Int): RIO[RecipeFormEditorService, Unit] =
    ZIO.serviceWithZIO[RecipeFormEditorService](_.updatePreparationTime(id, minutes))

  def updateWaitingTime(id: String, minutes: Int): RIO[RecipeFormEditorService, Unit] =
    ZIO.serviceWithZIO[RecipeFormEditorService](_.updateWaitingTime(id, minutes))

  def addIngredient(
    id: String,
    ingredient: String,
    amount: Int,
    unit: IngredientUnit
  ): RIO[RecipeFormEditorService, Unit] =
    ZIO.serviceWithZIO[RecipeFormEditorService](_.addIngredient(id, ingredient, amount, unit))

  def updateIngredient(
    id: String,
    ingredient: String,
    amount: Int,
    unit: IngredientUnit
  ): RIO[RecipeFormEditorService, Unit] =
    ZIO.serviceWithZIO[RecipeFormEditorService](_.updateIngredient(id, ingredient, amount, unit))

  def removeIngredient(id: String, ingredient: String): RIO[RecipeFormEditorService, Unit] =
    ZIO.serviceWithZIO[RecipeFormEditorService](_.removeIngredient(id, ingredient))

  def addTag(id: String, tag: String): RIO[RecipeFormEditorService, Unit] =
    ZIO.serviceWithZIO[RecipeFormEditorService](_.addTag(id, tag))

  def removeTag(id: String, tag: String): RIO[RecipeFormEditorService, Unit] =
    ZIO.serviceWithZIO[RecipeFormEditorService](_.removeTag(id, tag))

  def saveRecipeForm(id: String): RIO[RecipeFormEditorService, Unit] =
    ZIO.serviceWithZIO[RecipeFormEditorService](_.saveRecipeForm(id))

final class RecipeFormEditorServiceLive(recipeFormMaster: ActorRef[RecipeFormEditorCommand])(using
  actorSystem: ActorSystem[_],
  timeout: Timeout
) extends RecipeFormEditorService:

  def addRecipeForm(): Task[String] =
    ZIO
      .fromFuture { _ =>
        recipeFormMaster.ask[RecipeFormEditorResponse](ref => Create(ref))
      }
      .flatMap {
        case CreatedResponse(Right(reply)) => ZIO.succeed(reply)
        case _                             => ZIO.fail(Throwable("Failed to add recipe form"))
      }

  def getRecipeForm(id: String): Task[Summary] =
    ZIO
      .fromFuture { _ =>
        recipeFormMaster.ask[RecipeFormEditorResponse](ref => Get(id, ref))
      }
      .flatMap {
        case GetResponse(Right(reply)) => ZIO.succeed(reply)
        case _                         => ZIO.fail(Throwable(s"Failed to get recipe form $id"))
      }

  private def sendUpdateCommand(command: ActorRef[RecipeFormEditorResponse] => RecipeFormEditorCommand): Task[Unit] =
    ZIO
      .fromFuture { _ =>
        recipeFormMaster.ask[RecipeFormEditorResponse](ref => command(ref))
      }
      .flatMap {
        case DoneResponse(Right(true)) => ZIO.succeed(())
        case _                         => ZIO.fail(Throwable("Fail to update recipe form"))
      }

  def updateName(id: String, name: String): Task[Unit] =
    sendUpdateCommand(ref => UpdateName(id, name, ref))

  def updateDescription(id: String, description: String): Task[Unit] =
    sendUpdateCommand(ref => UpdateDescription(id, description, ref))

  def updateInstructions(id: String, instructions: String): Task[Unit] =
    sendUpdateCommand(ref => UpdateInstructions(id, instructions, ref))

  def updatePreparationTime(id: String, minutes: Int): Task[Unit] =
    sendUpdateCommand(ref => UpdatePreparationTime(id, minutes, ref))

  def updateWaitingTime(id: String, minutes: Int): Task[Unit] =
    sendUpdateCommand(ref => UpdateWaitingTime(id, minutes, ref))

  def addIngredient(id: String, ingredient: String, amount: Int, unit: IngredientUnit): Task[Unit] =
    sendUpdateCommand(ref => AddIngredient(id, ingredient, amount, unit.toString, ref))

  def updateIngredient(id: String, ingredient: String, amount: Int, unit: IngredientUnit): Task[Unit] =
    sendUpdateCommand(ref => AdjustIngredientAmount(id, ingredient, amount, unit.toString, ref))

  def removeIngredient(id: String, ingredient: String): Task[Unit] =
    sendUpdateCommand(ref => RemoveIngredient(id, ingredient, ref))

  def addTag(id: String, tag: String): Task[Unit] =
    sendUpdateCommand(ref => AddTag(id, tag, ref))

  def removeTag(id: String, tag: String): Task[Unit] =
    sendUpdateCommand(ref => RemoveTag(id, tag, ref))

  def saveRecipeForm(id: String): Task[Unit] =
    sendUpdateCommand(ref => Save(id, ref))

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
