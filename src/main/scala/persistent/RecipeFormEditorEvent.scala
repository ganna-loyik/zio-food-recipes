package persistent

import domain.*

sealed trait RecipeFormEditorEvent extends CborSerializable {
  def recipeFormId: String
}

final case class Created(recipeFormId: String) extends RecipeFormEditorEvent

final case class NameUpdated(recipeFormId: String, name: String)                 extends RecipeFormEditorEvent
final case class DescriptionUpdated(recipeFormId: String, description: String)   extends RecipeFormEditorEvent
final case class InstructionsUpdated(recipeFormId: String, instructions: String) extends RecipeFormEditorEvent
final case class PreparationTimeUpdated(recipeFormId: String, minutes: Int)      extends RecipeFormEditorEvent
final case class WaitingTimeUpdated(recipeFormId: String, minutes: Int)          extends RecipeFormEditorEvent

final case class IngridientAdded(recipeFormId: String, ingridient: String, amount: Int, unit: IngridientUnit)
  extends RecipeFormEditorEvent
final case class IngridientRemoved(recipeFormId: String, ingridient: String) extends RecipeFormEditorEvent
final case class IngridientAmountAdjusted(
  recipeFormId: String,
  ingridient: String,
  newAmount: Int,
  unit: IngridientUnit
) extends RecipeFormEditorEvent

final case class TagAdded(recipeFormId: String, tag: String)   extends RecipeFormEditorEvent
final case class TagRemoved(recipeFormId: String, tag: String) extends RecipeFormEditorEvent

final case class Saved(recipeFormId: String) extends RecipeFormEditorEvent
