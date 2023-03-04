package graphql.types

import caliban.schema.Annotations.GQLDefault
import domain.{RecipeFilters, RecipeSorting, SortingOrder}

case class GetRecipesInput(
  filters: Option[RecipeFilters] = None,
  @GQLDefault(RecipeSorting.Default.toString) sorting: RecipeSorting = RecipeSorting.Default,
  @GQLDefault(SortingOrder.Descending.toString) sortingOrder: SortingOrder = SortingOrder.Descending
)
