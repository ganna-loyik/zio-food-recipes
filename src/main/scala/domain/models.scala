package domain

import caliban.schema.Schema
import caliban.schema.Schema.{field, obj}

case class RecipeTagId(value: Long) extends AnyVal

case class RecipeTag(id: RecipeTagId = RecipeTagId(0), name: String)

object RecipeTag {
  given recipeTagSchema: Schema[Any, RecipeTag] = obj("RecipeTag")(implicit ft => List(field("name")(_.name)))
}

case class IngredientId(value: Long) extends AnyVal

case class Ingredient(id: IngredientId = IngredientId(0), name: String)

object Ingredient {
  given ingredientSchema: Schema[Any, Ingredient] =
    obj("Ingredient")(implicit ft => List(field("id")(_.id.value), field("name")(_.name)))
}

enum IngredientUnit(val abbreviated: String):
  case Gram       extends IngredientUnit("g")
  case Milliliter extends IngredientUnit("ml")

case class RecipeId(value: Long) extends AnyVal

case class Recipe(
  id: RecipeId = RecipeId(0),
  name: String,
  description: Option[String],
  instructions: String,
  preparationTimeMinutes: Int,
  waitingTimeMinutes: Int,
  tags: Set[String],
  ingredients: Map[String, (Int, IngredientUnit)]
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
      ),
      field("tags")(_.tags),
      field("ingredients")(r =>
        r.ingredients.map { case (ingr, (amount, unit)) => s"$ingr -- $amount ${unit.abbreviated}" }.mkString(",\n")
      )
    )
  )
}

case class RecipeFilters(
  name: Option[String],
  preparationTimeTo: Option[Int],
  waitingTimeTo: Option[Int],
  tags: Set[String],
  ingredients: Set[String]
)

enum RecipeSorting:
  case Default         extends RecipeSorting
  case Name            extends RecipeSorting
  case PreparationTime extends RecipeSorting

enum SortingOrder:
  case Ascending  extends SortingOrder
  case Descending extends SortingOrder
