package repo

import DbContext.*
import domain.*
import org.postgresql.util.PGobject

case class Recipe2TagDB(recipeId: RecipeId, tagId: RecipeTagId)

case class Recipe2IngredientDB(recipeId: RecipeId, ingredientId: IngredientId, amount: Int, unit: IngredientUnit)

case class RecipeDB(
  id: RecipeId,
  name: String,
  description: Option[String],
  instructions: String,
  preparationTimeMinutes: Int,
  waitingTimeMinutes: Int
):
  def toRecipe(
    tagDBs: Seq[RecipeTag],
    recipe2ingredientDBs: Seq[(Recipe2IngredientDB, Ingredient)]
  ): Recipe = {
    Recipe(
      id = id,
      name = name,
      description = description,
      instructions = instructions,
      preparationTimeMinutes = preparationTimeMinutes,
      waitingTimeMinutes = waitingTimeMinutes,
      tags = tagDBs.map(_.name).toSet,
      ingredients = recipe2ingredientDBs.map(row => row._2.name -> (row._1.amount, row._1.unit)).toMap
    )
  }

object RecipeDB:
  def fromRecipe(recipe: Recipe): RecipeDB = {
    RecipeDB(
      id = recipe.id,
      name = recipe.name,
      description = recipe.description,
      instructions = recipe.instructions,
      preparationTimeMinutes = recipe.preparationTimeMinutes,
      waitingTimeMinutes = recipe.waitingTimeMinutes
    )
  }
