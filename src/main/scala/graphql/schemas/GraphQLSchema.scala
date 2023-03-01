package graphql.schemas

import caliban.GraphQL
import service.*
import subscription.RecipeHub
import persistent.RecipeFormEditorService

object GraphQLSchema:
  val api: GraphQL[RecipeService & RecipeHub & RecipeTagService & IngridientService & RecipeFormEditorService] =
    RecipeSchema.api |+| RecipeTagSchema.api |+| IngridientSchema.api |+| RecipeFormEditorSchema.api
