package graphql.types

import domain.*

case class CreateRecipeInput(
  name: String,
  description: Option[String],
  instructions: String,
  preparationTimeMinutes: Int,
  waitingTimeMinutes: Int,
  tags: Set[String],
  ingredients: Seq[IngredientInput]
):
  def toRecipe: Recipe =
    Recipe(
      name = name,
      description = description,
      instructions = instructions,
      preparationTimeMinutes = preparationTimeMinutes,
      waitingTimeMinutes = waitingTimeMinutes,
      tags = tags,
      ingredients = ingredients.map(ingr => ingr.name -> (ingr.amount, ingr.unit)).toMap
    )
