package graphql.types

import domain.*

case class CreateIngredientInput(name: String):
  def toIngredient: Ingredient =
    Ingredient(name = name)
