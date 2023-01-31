package graphql.types

import domain.*

case class CreateRecipeInput(
  name: String,
  description: Option[String],
  instructions: String,
  preparationTimeMinutes: Int,
  waitingTimeMinutes: Int
):
  def toRecipe: Recipe = 
    Recipe(
      name = name,
      description = description,
      instructions = instructions,
      preparationTimeMinutes = preparationTimeMinutes,
      waitingTimeMinutes = waitingTimeMinutes,
      tags = Set(),
      ingridients = Map()
    )
