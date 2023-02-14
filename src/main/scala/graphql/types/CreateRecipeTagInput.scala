package graphql.types

import domain.*

case class CreateRecipeTagInput(
  name: String
):
  def toRecipeTag: RecipeTag =
    RecipeTag(name = name)
