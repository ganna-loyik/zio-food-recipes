package domain

import caliban.schema.Schema
import caliban.schema.Schema.{field, obj}

case class RecipeTagId(value: Long) extends AnyVal

case class RecipeTag(id: RecipeTagId = RecipeTagId(0), name: String)

object RecipeTag {
  given recipeTagSchema: Schema[Any, RecipeTag] = obj("RecipeTag")(implicit ft => List(field("name")(_.name)))
}

case class IngridientId(value: Long) extends AnyVal

case class Ingridient(id: IngridientId = IngridientId(0), name: String)

object Ingridient {
  given ingridientSchema: Schema[Any, Ingridient] =
    obj("Ingridient")(implicit ft => List(field("id")(_.id.value), field("name")(_.name)))
}

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
