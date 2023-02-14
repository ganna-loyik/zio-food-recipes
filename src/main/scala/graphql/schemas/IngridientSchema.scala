package graphql.schemas

import caliban.GraphQL.graphQL
import caliban.RootResolver
import zio.*
import domain.*
import graphql.types.*
import Ingridient.ingridientSchema
import service.IngridientService
import caliban.schema.Schema

object IngridientSchema:
  case class Queries(
    ingridient: IdArg => URIO[IngridientService, Option[Ingridient]],
    ingridients: URIO[IngridientService, List[Ingridient]]
  )

  case class Mutations(
    addIngridient: CreateIngridientInput => URIO[IngridientService, Long],
    // updateIngridient: UpdateIngridientInput => ZIO[IngridientService, String, Unit],
    deleteIngridient: IdArg => URIO[IngridientService, Unit]
  )

  val queries = Queries(
    arg => IngridientService.getIngridientById(IngridientId(arg.id)),
    IngridientService.getAllIngridients()
  )

  val mutations = Mutations(
    form => IngridientService.addIngridient(form.toIngridient).map(_.value),
    // form => IngridientService.updateIngridient(form.toIngridient).mapError(_.msg),
    arg => IngridientService.deleteIngridient(IngridientId(arg.id))
  )

  val api = graphQL[IngridientService, Queries, Mutations, Unit](RootResolver(queries, mutations))
