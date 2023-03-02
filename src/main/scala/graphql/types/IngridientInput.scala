package graphql.types

import domain.IngredientUnit

case class IngredientInput(name: String, amount: Int, unit: IngredientUnit)
