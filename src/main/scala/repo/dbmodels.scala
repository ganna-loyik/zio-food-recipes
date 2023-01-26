package repo

import domain.*
import org.postgresql.util.PGobject

case class Recipe2TagDB(recipeId: RecipeId, tagId: TagId)

case class Recipe2IngridientDB(recipeId: RecipeId, ingridientId: IngridientId, amount: Int, unit: IngridientUnit)

case class RecipeDB(
  id: RecipeId,
  name: String,
  description: Option[String],
  instructions: String,
  preparationTimeMinutes: Int,
  waitingTimeMinutes: Int
):
  def toRecipe(
    tagDBs: Seq[Tag],
    recipe2ingridientDBs: Seq[(Recipe2IngridientDB, Ingridient)]
  ): Recipe = {
    Recipe(
      id = id,
      name = name,
      description = description,
      instructions = instructions,
      preparationTimeMinutes = preparationTimeMinutes,
      waitingTimeMinutes = waitingTimeMinutes,
      tags = tagDBs.map(_.name).toSet,
      ingridients = recipe2ingridientDBs.map(row => row._2.name -> (row._1.amount, row._1.unit)).toMap
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
