package graphql.schemas

import caliban.GraphQL
import service.*
import subscription.RecipeHub

object GraphQLSchema:
  val api: GraphQL[RecipeService & RecipeHub & RecipeTagService & IngridientService] =
    RecipeSchema.api |+| RecipeTagSchema.api |+| IngridientSchema.api
