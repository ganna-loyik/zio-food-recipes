package graphql.types.editor

import domain.IngredientUnit

case class UpdateIngredientInput(id: String, ingredient: String, amount: Int, unit: IngredientUnit)
