package graphql.schemas

import caliban.CalibanError.ExecutionError
import caliban.graphQL
import caliban.RootResolver
import caliban.schema.ArgBuilder.auto.*
import caliban.schema.Schema.auto.*
import zio.*
import domain.*
import graphql.types.editor.*
import persistent.common.*
import persistent.master.*

object RecipeFormEditorSchema:
  case class Queries(getRecipeForm: RecipeFormIdArg => RIO[RecipeFormEditorService, Summary])

  case class Mutations(
    addRecipeForm: RIO[RecipeFormEditorService, String],
    updateNameInRecipeForm: UpdateNameInput => RIO[RecipeFormEditorService, Unit],
    updateDescriptionInRecipeForm: UpdateDescriptionInput => RIO[RecipeFormEditorService, Unit],
    updateInstructionsInRecipeForm: UpdateInstructionsInput => RIO[RecipeFormEditorService, Unit],
    updatePreparationTimeInRecipeForm: UpdateTimeInput => RIO[RecipeFormEditorService, Unit],
    updateWaitingTimeInRecipeForm: UpdateTimeInput => RIO[RecipeFormEditorService, Unit],
    addIngredientToRecipeForm: UpdateIngredientInput => RIO[RecipeFormEditorService, Unit],
    updateIngredientInRecipeForm: UpdateIngredientInput => RIO[RecipeFormEditorService, Unit],
    deleteIngredientFromRecipeForm: RemoveIngredientInput => RIO[RecipeFormEditorService, Unit],
    addTagToRecipeForm: AddTagInput => RIO[RecipeFormEditorService, Unit],
    deleteTagFromRecipeForm: RemoveTagInput => RIO[RecipeFormEditorService, Unit],
    saveRecipeForm: RecipeFormIdArg => RIO[RecipeFormEditorService, Unit]
  )

  val queries =
    Queries(arg => RecipeFormEditorService.getRecipeForm(arg.id).mapError(e => ExecutionError(e.getMessage)))

  val mutations = Mutations(
    RecipeFormEditorService.addRecipeForm().mapError(e => ExecutionError(e.getMessage)),
    form => RecipeFormEditorService.updateName(form.id, form.name).mapError(e => ExecutionError(e.getMessage)),
    form =>
      RecipeFormEditorService.updateDescription(form.id, form.description).mapError(e => ExecutionError(e.getMessage)),
    form =>
      RecipeFormEditorService
        .updateInstructions(form.id, form.instructions)
        .mapError(e => ExecutionError(e.getMessage)),
    form =>
      RecipeFormEditorService.updatePreparationTime(form.id, form.minutes).mapError(e => ExecutionError(e.getMessage)),
    form =>
      RecipeFormEditorService.updateWaitingTime(form.id, form.minutes).mapError(e => ExecutionError(e.getMessage)),
    form =>
      RecipeFormEditorService
        .addIngredient(form.id, form.ingredient, form.amount, form.unit)
        .mapError(e => ExecutionError(e.getMessage)),
    form =>
      RecipeFormEditorService
        .updateIngredient(form.id, form.ingredient, form.amount, form.unit)
        .mapError(e => ExecutionError(e.getMessage)),
    form =>
      RecipeFormEditorService.removeIngredient(form.id, form.ingredient).mapError(e => ExecutionError(e.getMessage)),
    form => RecipeFormEditorService.addTag(form.id, form.tag).mapError(e => ExecutionError(e.getMessage)),
    form => RecipeFormEditorService.removeTag(form.id, form.tag).mapError(e => ExecutionError(e.getMessage)),
    arg => RecipeFormEditorService.saveRecipeForm(arg.id).mapError(e => ExecutionError(e.getMessage))
  )

  val api = graphQL[RecipeFormEditorService, Queries, Mutations, Unit](RootResolver(queries, mutations))
