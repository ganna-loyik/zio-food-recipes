package domain

import caliban.schema.Schema
import caliban.schema.Schema.{field, obj}

case class TagId(value: Long) extends AnyVal

case class Tag(id: TagId, name: String)

case class IngridientId(value: Long) extends AnyVal

case class Ingridient(id: IngridientId, name: String)

enum IngridientUnit(val abbreviated: String):
  case Gram       extends IngridientUnit("g")
  case Milliliter extends IngridientUnit("ml")

case class RecipeId(value: Long) extends AnyVal

case class Recipe(
  id: RecipeId = RecipeId(0),
  name: String,
  description: Option[String],
  instructions: String,
  preparationTimeMinutes: Int,
  waitingTimeMinutes: Int,
  tags: Set[String],
  ingridients: Map[String, (Int, IngridientUnit)]
)

object Recipe {
  given recipeSchema: Schema[Any, Recipe] = obj("Recipe")(implicit ft =>
    List(
      field("id")(_.id.value),
      field("name")(_.name),
      field("description")(_.description),
      field("instructions")(_.instructions),
      field("preparationTimeMinutes")(_.preparationTimeMinutes),
      field("waitingTimeMinutes")(_.waitingTimeMinutes),
      field("time")(r =>
        s"It takes ${r.preparationTimeMinutes} min for prepare and then ${r.waitingTimeMinutes} min to wait"
      )
    )
  )
}
