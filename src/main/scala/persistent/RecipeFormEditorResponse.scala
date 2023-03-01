package persistent

import akka.Done
import akka.pattern.StatusReply

sealed trait RecipeFormEditorResponse

case class CreatedResponse(id: StatusReply[String]) extends RecipeFormEditorResponse

case class Summary(form: RecipeForm, isSaved: Boolean)
case class GetResponse(summary: StatusReply[Summary]) extends RecipeFormEditorResponse

case class DoneResponse(done: StatusReply[Done]) extends RecipeFormEditorResponse
