package persistent

import domain.*
import akka.actor.typed.ActorRef
import akka.Done
import akka.pattern.StatusReply

sealed trait RecipeFormEditorCommand extends CborSerializable

sealed trait UpdateCommand {
  val id: String
  val replyTo: ActorRef[RecipeFormEditorResponse]
}

final case class Create(replyTo: ActorRef[RecipeFormEditorResponse]) extends RecipeFormEditorCommand

final case class UpdateName(id: String, name: String, replyTo: ActorRef[RecipeFormEditorResponse])
  extends RecipeFormEditorCommand
     with UpdateCommand

final case class UpdateDescription(id: String, description: String, replyTo: ActorRef[RecipeFormEditorResponse])
  extends RecipeFormEditorCommand
     with UpdateCommand

final case class UpdateInstructions(id: String, instructions: String, replyTo: ActorRef[RecipeFormEditorResponse])
  extends RecipeFormEditorCommand
     with UpdateCommand

final case class UpdatePreparationTime(id: String, minutes: Int, replyTo: ActorRef[RecipeFormEditorResponse])
  extends RecipeFormEditorCommand
     with UpdateCommand

final case class UpdateWaitingTime(id: String, minutes: Int, replyTo: ActorRef[RecipeFormEditorResponse])
  extends RecipeFormEditorCommand
     with UpdateCommand

final case class AddIngridient(
  id: String,
  ingridient: String,
  amount: Int,
  unit: IngridientUnit,
  replyTo: ActorRef[RecipeFormEditorResponse]
) extends RecipeFormEditorCommand
     with UpdateCommand

final case class RemoveIngridient(id: String, ingridient: String, replyTo: ActorRef[RecipeFormEditorResponse])
  extends RecipeFormEditorCommand
     with UpdateCommand

final case class AdjustIngridientAmount(
  id: String,
  ingridient: String,
  amount: Int,
  unit: IngridientUnit,
  replyTo: ActorRef[RecipeFormEditorResponse]
) extends RecipeFormEditorCommand
     with UpdateCommand

final case class AddTag(id: String, tag: String, replyTo: ActorRef[RecipeFormEditorResponse])
  extends RecipeFormEditorCommand
     with UpdateCommand

final case class RemoveTag(id: String, tag: String, replyTo: ActorRef[RecipeFormEditorResponse])
  extends RecipeFormEditorCommand
     with UpdateCommand

final case class Save(id: String, replyTo: ActorRef[RecipeFormEditorResponse]) extends RecipeFormEditorCommand

final case class Get(id: String, replyTo: ActorRef[RecipeFormEditorResponse]) extends RecipeFormEditorCommand
