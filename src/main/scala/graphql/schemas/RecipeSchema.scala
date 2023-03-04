package graphql.schemas

import caliban.GraphQL.graphQL
import caliban.RootResolver
import zio.*
import zio.stream.{UStream, ZStream}
import domain.*
import graphql.types.*
import Recipe.recipeSchema
import service.RecipeService
import subscription.RecipeHub

object RecipeSchema:
  case class Queries(recipe: IdArg => URIO[RecipeService, Option[Recipe]], recipes: GetRecipesInput => URIO[RecipeService, List[Recipe]])

  case class Mutations(
    addRecipe: CreateRecipeInput => URIO[RecipeService & RecipeHub, Long],
    updateRecipe: UpdateRecipeInput => RIO[RecipeService, Unit],
    deleteRecipe: IdArg => URIO[RecipeService, Unit]
  )

  case class Subscriptions(newRecipe: ZStream[RecipeHub, Nothing, Recipe])

  val queries = Queries(
    arg => RecipeService.getRecipeById(RecipeId(arg.id)),
    form => RecipeService.getAllRecipes(form.filters, form.sorting, form.sortingOrder)
  )

  val mutations = Mutations(
    form =>
      for {
        recipeId <- RecipeService.addRecipe(form.toRecipe).map(_.value)
        recipe   <- RecipeService.getRecipeById(RecipeId(recipeId))
        _        <- RecipeHub.publishRecipe(recipe.get)
      } yield recipeId,
    form => RecipeService.updateRecipe(form.toRecipe).mapError(e => Throwable(e.msg)),
    arg => RecipeService.deleteRecipe(RecipeId(arg.id))
  )

  val subscriptions = Subscriptions(RecipeHub.subscribe())

  val api = graphQL[RecipeService & RecipeHub, Queries, Mutations, Subscriptions](
    RootResolver(queries, mutations, subscriptions)
  )
