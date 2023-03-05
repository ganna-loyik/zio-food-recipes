package persistent.master

import akka.actor.typed.ActorRef
import persistent.common.*

case class RecipeFormMasterState(editors: Map[String, ActorRef[RecipeFormEditorCommand]]) extends CborSerializable {
  def addEditor(id: String, actorRef: ActorRef[RecipeFormEditorCommand]): RecipeFormMasterState =
    copy(editors = editors + (id -> actorRef))
}
