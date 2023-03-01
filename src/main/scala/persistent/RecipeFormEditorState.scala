package persistent

import domain.IngridientUnit

trait RecipeFormEditorState

final case class RecipeFormOpenState(id: String, form: RecipeForm) extends RecipeFormEditorState {
  def isCompleted: Boolean =
    form.name.getOrElse("").nonEmpty &&
    form.instructions.nonEmpty &&
    form.preparationTimeMinutes.nonEmpty &&
    form.waitingTimeMinutes.nonEmpty &&
    form.ingridients.nonEmpty

  def updateName(name: String): RecipeFormEditorState = copy(form = form.copy(name = Some(name)))

  def updateDescription(description: String): RecipeFormEditorState =
    copy(form = form.copy(description = Some(description)))

  def updateInstructions(instructions: String): RecipeFormEditorState =
    copy(form = form.copy(instructions = Some(instructions)))

  def updatePrepationTime(minutes: Int): RecipeFormEditorState =
    copy(form = form.copy(preparationTimeMinutes = Some(minutes)))

  def updateWaitingTime(minutes: Int): RecipeFormEditorState =
    copy(form = form.copy(waitingTimeMinutes = Some(minutes)))

  def hasIngridient(ingridient: String): Boolean =
    form.ingridients.contains(ingridient)

  def updateIngridient(ingridient: String, amount: Int, unit: IngridientUnit): RecipeFormEditorState =
    amount match {
      case 0 => copy(form = form.copy(ingridients = form.ingridients - ingridient))
      case _ => copy(form = form.copy(ingridients = form.ingridients + (ingridient -> (amount, unit))))
    }

  def removeIngridient(ingridient: String): RecipeFormEditorState =
    copy(form = form.copy(ingridients = form.ingridients - ingridient))

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
