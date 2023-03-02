package persistent

import akka.actor.typed.ActorRef

case class RecipeFormMasterState(editors: Map[String, ActorRef[RecipeFormEditorCommand]]) {
  def addEditor(id: String, actorRef: ActorRef[RecipeFormEditorCommand]): RecipeFormMasterState =
    copy(editors = editors + (id -> actorRef))
}