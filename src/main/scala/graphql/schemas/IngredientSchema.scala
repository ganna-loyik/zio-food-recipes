package graphql.schemas

import caliban.CalibanError.ExecutionError
import caliban.GraphQL.graphQL
import caliban.RootResolver
import zio.*
import domain.*
import graphql.types.*
import Ingredient.ingredientSchema
import service.IngredientService

object IngredientSchema:
  case class Queries(
    ingredients: URIO[IngredientService, List[Ingredient]]
  )

  case class Mutations(
    addIngredient: CreateIngredientInput => URIO[IngredientService, Long],
    updateIngredient: UpdateIngredientInput => RIO[IngredientService, Unit],
    deleteIngredient: IdArg => URIO[IngredientService, Unit]
  )

  val queries = Queries(
    IngredientService.getAllIngredients()
  )

  val mutations = Mutations(
    form => IngredientService.addIngredient(form.toIngredient).map(_.value),
    form => IngredientService.updateIngredient(form.toIngredient).mapError(e => ExecutionError(e.msg)),
    arg => IngredientService.deleteIngredient(IngredientId(arg.id))
  )

  val api = graphQL[IngredientService, Queries, Mutations, Unit](RootResolver(queries, mutations))
