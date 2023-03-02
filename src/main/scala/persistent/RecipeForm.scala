package persistent

import domain.*

case class RecipeForm(
  name: Option[String],
  description: Option[String],
  instructions: Option[String],
  preparationTimeMinutes: Option[Int],
  waitingTimeMinutes: Option[Int],
  tags: Set[String],
  ingredients: Map[String, (Int, IngredientUnit)]
)
