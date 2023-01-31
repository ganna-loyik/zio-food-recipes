package graphql.schemas

import caliban.GraphQL
import service.RecipeService

object GraphQLSchema:
  val api: GraphQL[RecipeService] = RecipeSchema.api
