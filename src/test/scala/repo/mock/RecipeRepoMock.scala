package repo.mock

import zio.*
import zio.mock.*
import domain.*
import domain.DomainError.*
import repo.RecipeRepository

object RecipeRepoMock extends Mock[RecipeRepository]:
  object Add     extends Effect[Recipe, Nothing, RecipeId]
  object Delete  extends Effect[RecipeId, Nothing, Unit]
  object GetAll  extends Effect[(Option[RecipeFilters], RecipeSorting, SortingOrder), Nothing, List[Recipe]]
  object GetById extends Effect[RecipeId, Nothing, Option[Recipe]]
  object Update  extends Effect[Recipe, Nothing, Unit]

  val compose: URLayer[Proxy, RecipeRepository] =
    ZLayer.fromFunction { (proxy: Proxy) =>
      new RecipeRepository {
        override def add(recipe: Recipe) = proxy(Add, recipe)

        override def delete(id: RecipeId) = proxy(Delete, id)

        override def getAll(filters: Option[RecipeFilters], sorting: RecipeSorting, sortingOrder: SortingOrder) =
          proxy(GetAll, (filters, sorting, sortingOrder))

        override def getById(id: RecipeId) = proxy(GetById, id)

        override def update(recipe: Recipe) = proxy(Update, recipe)
      }
    }
