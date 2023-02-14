package graphql.types

import domain.*

case class UpdateIngridientInput(id: Long, name: String):
  def toIngridient: Ingridient =
    Ingridient(id = IngridientId(id), name = name)
