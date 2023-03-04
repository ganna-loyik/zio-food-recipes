package repo

import domain.*
import io.getquill.*

inline def recipes = quote {
  querySchema[RecipeDB](
    "recipes",
    _.id                     -> "id",
    _.name                   -> "name",
    _.description            -> "description",
    _.instructions           -> "instructions",
    _.preparationTimeMinutes -> "preparation_time",
    _.waitingTimeMinutes     -> "waiting_time"
  )
}

inline def recipesDynamic = recipes.dynamic

inline def tags = quote(querySchema[RecipeTag]("tags"))

inline def ingredients = quote(querySchema[Ingredient]("ingredients"))

inline def recipe2tags = quote {
  querySchema[Recipe2TagDB](
    "recipe2tags",
    _.recipeId -> "recipe_id",
    _.tagId    -> "tag_id"
  )
}

inline def recipe2ingredients = quote {
  querySchema[Recipe2IngredientDB](
    "recipe2ingredients",
    _.recipeId     -> "recipe_id",
    _.ingredientId -> "ingredient_id",
    _.amount       -> "amount",
    _.unit         -> "unit"
  )
}
