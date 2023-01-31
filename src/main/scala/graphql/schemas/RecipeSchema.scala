package graphql.schemas

import caliban.GraphQL.graphQL
import caliban.RootResolver
import zio.*
import domain.*
import graphql.types.*
import Recipe.recipeSchema
import service.RecipeService
import caliban.schema.Schema

object RecipeSchema:
  case class Queries(recipe: IdArg => URIO[RecipeService, Option[Recipe]], recipes: URIO[RecipeService, List[Recipe]])

  case class Mutations(
    addRecipe: CreateRecipeInput => URIO[RecipeService, Long],
    // updateRecipe: UpdateRecipeInput => ZIO[RecipeService, String, Unit],
    deleteRecipe: IdArg => URIO[RecipeService, Unit]
  )

  val queries = Queries(
    arg => RecipeService.getRecipeById(RecipeId(arg.id)),
    RecipeService.getAllRecipes()
  )

  val mutations = Mutations(
    form => RecipeService.addRecipe(form.toRecipe).map(_.value),
    // form => RecipeService.updateRecipe(form.toRecipe).mapError(_.msg),
    arg => RecipeService.deleteRecipe(RecipeId(arg.id))
  )

  val api = graphQL[RecipeService, Queries, Mutations, Unit](RootResolver(queries, mutations))
