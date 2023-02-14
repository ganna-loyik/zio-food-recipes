package graphql.types

import domain.*

case class CreateIngridientInput(name: String):
  def toIngridient: Ingridient =
    Ingridient(name = name)
