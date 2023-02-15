package graphql.schemas

import caliban.GraphQL.graphQL
import caliban.RootResolver
import zio.*
import domain.*
import graphql.types.*
import Ingridient.ingridientSchema
import service.IngridientService

object IngridientSchema:
  case class Queries(
    ingridients: URIO[IngridientService, List[Ingridient]]
  )

  case class Mutations(
    addIngridient: CreateIngridientInput => URIO[IngridientService, Long],
    updateIngridient: UpdateIngridientInput => RIO[IngridientService, Unit],
    deleteIngridient: IdArg => URIO[IngridientService, Unit]
  )

  val queries = Queries(
    IngridientService.getAllIngridients()
  )

  val mutations = Mutations(
    form => IngridientService.addIngridient(form.toIngridient).map(_.value),
    form => IngridientService.updateIngridient(form.toIngridient).mapError(e => Throwable(e.msg)),
    arg => IngridientService.deleteIngridient(IngridientId(arg.id))
  )

  val api = graphQL[IngridientService, Queries, Mutations, Unit](RootResolver(queries, mutations))
