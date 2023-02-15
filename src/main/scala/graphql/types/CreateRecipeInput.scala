package graphql.types

import domain.*

case class CreateRecipeInput(
  name: String,
  description: Option[String],
  instructions: String,
  preparationTimeMinutes: Int,
  waitingTimeMinutes: Int,
  tags: Set[String],
  ingridients: Seq[IngridientInput]
):
  def toRecipe: Recipe =
    Recipe(
      name = name,
      description = description,
      instructions = instructions,
      preparationTimeMinutes = preparationTimeMinutes,
      waitingTimeMinutes = waitingTimeMinutes,
      tags = tags,
      ingridients = ingridients.map(ingr => ingr.name -> (ingr.amount, ingr.unit)).toMap
    )
