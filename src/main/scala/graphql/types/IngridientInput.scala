package graphql.types

import domain.IngridientUnit

case class IngridientInput(name: String, amount: Int, unit: IngridientUnit)