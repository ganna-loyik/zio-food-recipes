package graphql.types

import domain.*

case class UpdateIngredientInput(id: Long, name: String):
  def toIngredient: Ingredient =
    Ingredient(id = IngredientId(id), name = name)
