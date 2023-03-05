package persistent

sealed trait RecipeFormEditorResponse extends CborSerializable

case class CreatedResponse(id: Either[Throwable, String]) extends RecipeFormEditorResponse

case class Summary(form: RecipeForm, isSaved: Boolean)      extends CborSerializable
case class GetResponse(summary: Either[Throwable, Summary]) extends RecipeFormEditorResponse

case class DoneResponse(done: Either[Throwable, Boolean]) extends RecipeFormEditorResponse
