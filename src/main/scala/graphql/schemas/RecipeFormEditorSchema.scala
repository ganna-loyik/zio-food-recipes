package graphql.schemas

import caliban.GraphQL.graphQL
import caliban.RootResolver
import zio.*
import domain.*
import graphql.types.editor.*
import persistent.*

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

  val queries = Queries(arg => RecipeFormEditorService.getRecipeForm(arg.id))

  val mutations = Mutations(
    RecipeFormEditorService.addRecipeForm(),
    form => RecipeFormEditorService.updateName(form.id, form.name),
    form => RecipeFormEditorService.updateDescription(form.id, form.description),
    form => RecipeFormEditorService.updateInstructions(form.id, form.instructions),
    form => RecipeFormEditorService.updatePreparationTime(form.id, form.minutes),
    form => RecipeFormEditorService.updateWaitingTime(form.id, form.minutes),
    form => RecipeFormEditorService.addIngredient(form.id, form.ingredient, form.amount, form.unit),
    form => RecipeFormEditorService.updateIngredient(form.id, form.ingredient, form.amount, form.unit),
    form => RecipeFormEditorService.removeIngredient(form.id, form.ingredient),
    form => RecipeFormEditorService.addTag(form.id, form.tag),
    form => RecipeFormEditorService.removeTag(form.id, form.tag),
    arg => RecipeFormEditorService.saveRecipeForm(arg.id)
  )

  val api = graphQL[RecipeFormEditorService, Queries, Mutations, Unit](RootResolver(queries, mutations))
