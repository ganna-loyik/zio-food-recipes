package api

import zio.json.*

object protocol:

  final case class UpdateRecipe(name: String, description: Option[String])
  object UpdateRecipe:
    implicit val updateRecipeDecoder: JsonDecoder[UpdateRecipe] = DeriveJsonDecoder.gen[UpdateRecipe]

  final case class CreateRecipe(name: String, description: Option[String])
  object CreateRecipe:
    implicit val createRecipeDecoder: JsonDecoder[CreateRecipe] = DeriveJsonDecoder.gen[CreateRecipe]

  final case class GetRecipes(recipes: List[GetRecipe])
  object GetRecipes:
    implicit val getRecipesEncoder: JsonEncoder[GetRecipes] = DeriveJsonEncoder.gen[GetRecipes]

  final case class GetRecipe(id: Long, name: String, description: Option[String])
  object GetRecipe:
    implicit val recipeCreatedEncoder: JsonEncoder[GetRecipe] = DeriveJsonEncoder.gen[GetRecipe]
