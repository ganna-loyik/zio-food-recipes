package graphql.schemas

import caliban.GraphQL
import service.*

object GraphQLSchema:
  val api: GraphQL[RecipeService with RecipeTagService with IngridientService] =
    RecipeSchema.api |+| RecipeTagSchema.api |+| IngridientSchema.api
