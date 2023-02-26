package persistent

import domain.*
import akka.actor.typed.ActorRef
import akka.Done
import akka.pattern.StatusReply

sealed trait RecipeFormEditorCommand
final case class UpdateName(name: String, replyTo: ActorRef[Done])                 extends RecipeFormEditorCommand
final case class UpdateDescription(description: String, replyTo: ActorRef[Done])   extends RecipeFormEditorCommand
final case class UpdateInstructions(instructions: String, replyTo: ActorRef[Done]) extends RecipeFormEditorCommand
final case class UpdatePreparationTime(minutes: Int, replyTo: ActorRef[Done])      extends RecipeFormEditorCommand
final case class UpdateWaitingTime(minutes: Int, replyTo: ActorRef[Done])          extends RecipeFormEditorCommand

final case class AddIngridient(
  ingridient: String,
  amount: Int,
  unit: IngridientUnit,
  replyTo: ActorRef[StatusReply[Done]]
) extends RecipeFormEditorCommand
final case class RemoveIngridient(ingridient: String, replyTo: ActorRef[StatusReply[Done]])
  extends RecipeFormEditorCommand
final case class AdjustIngridientAmount(
  ingridient: String,
  amount: Int,
  unit: IngridientUnit,
  replyTo: ActorRef[StatusReply[Done]]
) extends RecipeFormEditorCommand

final case class AddTag(tag: String, replyTo: ActorRef[StatusReply[Done]])    extends RecipeFormEditorCommand
final case class RemoveTag(tag: String, replyTo: ActorRef[StatusReply[Done]]) extends RecipeFormEditorCommand

final case class Save(replyTo: ActorRef[StatusReply[Done]]) extends RecipeFormEditorCommand

final case class Get(replyTo: ActorRef[Summary]) extends RecipeFormEditorCommand

final case class Summary(form: RecipeForm, isSaved: Boolean)
