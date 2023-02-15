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

inline def tags = quote(querySchema[RecipeTag]("tags"))

inline def ingridients = quote(querySchema[Ingridient]("ingridients"))

inline def recipe2tags = quote {
  querySchema[Recipe2TagDB](
    "recipe2tags",
    _.recipeId -> "recipe_id",
    _.tagId    -> "tag_id"
  )
}

inline def recipe2ingridients = quote {
  querySchema[Recipe2IngridientDB](
    "recipe2ingridients",
    _.recipeId     -> "recipe_id",
    _.ingridientId -> "ingridient_id",
    _.amount       -> "amount",
    _.unit         -> "unit"
  )
}
