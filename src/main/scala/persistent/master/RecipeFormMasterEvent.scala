package persistent.master

import persistent.common.CborSerializable

sealed trait RecipeFormMasterEvent extends CborSerializable

final case class RecipeFormEditorCreated(recipeFormId: String) extends RecipeFormMasterEvent
