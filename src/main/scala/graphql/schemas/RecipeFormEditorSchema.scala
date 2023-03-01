package graphql.schemas

import caliban.GraphQL.graphQL
import caliban.RootResolver
import zio.*
import domain.*
import graphql.types.*
import persistent.RecipeFormEditorService
import caliban.schema.Schema

object RecipeFormEditorSchema:
  case class Queries()

  case class Mutations(
    addRecipeFormEditor: RIO[RecipeFormEditorService, String]
  )

  val queries = Queries()

  val mutations = Mutations(
    RecipeFormEditorService.addRecipeFormEditor()
  )

  val api = graphQL[RecipeFormEditorService, Queries, Mutations, Unit](RootResolver(queries, mutations))
