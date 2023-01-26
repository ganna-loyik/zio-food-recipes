package domain

case class TagId(value: Long) extends AnyVal

case class Tag(id: TagId, name: String)

case class IngridientId(value: Long) extends AnyVal

case class Ingridient(id: IngridientId, name: String)

enum IngridientUnit(val abbreviated: String):
  case Gram extends IngridientUnit("g") 
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
