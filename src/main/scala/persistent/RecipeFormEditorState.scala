package persistent

import domain.IngredientUnit

trait RecipeFormEditorState

final case class RecipeFormOpenState(id: String, form: RecipeForm) extends RecipeFormEditorState {
  def isCompleted: Boolean =
    form.name.getOrElse("").nonEmpty &&
    form.instructions.nonEmpty &&
    form.preparationTimeMinutes.nonEmpty &&
    form.waitingTimeMinutes.nonEmpty &&
    form.ingredients.nonEmpty

  def updateName(name: String): RecipeFormEditorState = copy(form = form.copy(name = Some(name)))

  def updateDescription(description: String): RecipeFormEditorState =
    copy(form = form.copy(description = Some(description)))

  def updateInstructions(instructions: String): RecipeFormEditorState =
    copy(form = form.copy(instructions = Some(instructions)))

  def updatePrepationTime(minutes: Int): RecipeFormEditorState =
    copy(form = form.copy(preparationTimeMinutes = Some(minutes)))

  def updateWaitingTime(minutes: Int): RecipeFormEditorState =
    copy(form = form.copy(waitingTimeMinutes = Some(minutes)))

  def hasIngredient(ingredient: String): Boolean =
    form.ingredients.contains(ingredient)

  def updateIngredient(ingredient: String, amount: Int, unit: IngredientUnit): RecipeFormEditorState =
    amount match {
      case 0 => copy(form = form.copy(ingredients = form.ingredients - ingredient))
      case _ => copy(form = form.copy(ingredients = form.ingredients + (ingredient -> (amount, unit))))
    }

  def removeIngredient(ingredient: String): RecipeFormEditorState =
    copy(form = form.copy(ingredients = form.ingredients - ingredient))

  def hasTag(tag: String): Boolean =
    form.tags.contains(tag)

  def addTag(tag: String): RecipeFormEditorState = copy(form = form.copy(tags = form.tags + tag))

  def removeTag(tag: String): RecipeFormEditorState = copy(form = form.copy(tags = form.tags - tag))

  def toSummary: Summary = Summary(form, false)
}

object RecipeFormOpenState {
  def empty(id: String) = RecipeFormOpenState(id, RecipeForm(None, None, None, None, None, Set(), Map()))
}

final case class RecipeFormSavedState(id: String, form: RecipeForm) extends RecipeFormEditorState {
  def toSummary: Summary = Summary(form, true)
}
