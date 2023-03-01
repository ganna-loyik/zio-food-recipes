package persistent

sealed trait RecipeFormMasterEvent extends CborSerializable

final case class RecipeFormEditorCreated(recipeFormId: String) extends RecipeFormMasterEvent
