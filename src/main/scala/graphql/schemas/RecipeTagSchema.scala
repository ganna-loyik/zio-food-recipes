package graphql.schemas

import caliban.graphQL
import caliban.RootResolver
import caliban.schema.ArgBuilder.auto.*
import caliban.schema.Schema.auto.*
import zio.*
import domain.*
import graphql.types.*
import RecipeTag.recipeTagSchema
import service.RecipeTagService

object RecipeTagSchema:
  case class Queries(recipeTags: URIO[RecipeTagService, List[RecipeTag]])

  case class Mutations(
    addRecipeTag: CreateRecipeTagInput => URIO[RecipeTagService, Long],
    deleteRecipeTag: IdArg => URIO[RecipeTagService, Unit]
  )

  val queries = Queries(
    RecipeTagService.getAllRecipeTags()
  )

  val mutations = Mutations(
    form => RecipeTagService.addRecipeTag(form.toRecipeTag).map(_.value),
    arg => RecipeTagService.deleteRecipeTag(RecipeTagId(arg.id))
  )

  val api = graphQL[RecipeTagService, Queries, Mutations, Unit](RootResolver(queries, mutations))
