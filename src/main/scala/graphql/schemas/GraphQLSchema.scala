package graphql.schemas

import caliban.GraphQL
import service.*
import subscription.RecipeHub
import persistent.RecipeFormEditorService

object GraphQLSchema:
  val api: GraphQL[RecipeService & RecipeHub & RecipeTagService & IngredientService & RecipeFormEditorService] =
    RecipeSchema.api |+| RecipeTagSchema.api |+| IngredientSchema.api |+| RecipeFormEditorSchema.api
