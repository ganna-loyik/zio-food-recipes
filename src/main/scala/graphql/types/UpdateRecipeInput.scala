package graphql.types

import domain.*

case class UpdateRecipeInput(
  id: Long,
  name: String,
  description: Option[String],
  instructions: String,
  preparationTimeMinutes: Int,
  waitingTimeMinutes: Int
):
  def toRecipe: Recipe =
    Recipe(
      id = RecipeId(id),
      name = name,
      description = description,
      instructions = instructions,
      preparationTimeMinutes = preparationTimeMinutes,
      waitingTimeMinutes = waitingTimeMinutes,
      tags = Set(),
      ingredients = Map()
    )
