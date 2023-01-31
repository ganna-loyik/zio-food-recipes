package api

import zio.json.*

object protocol:

  final case class GetRecipe(id: Long, name: String, description: Option[String], instructions: String)
  object GetRecipe:
    implicit val recipeCreatedEncoder: JsonEncoder[GetRecipe] = DeriveJsonEncoder.gen[GetRecipe]
