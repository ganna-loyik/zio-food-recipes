package repo.mock

import zio.*
import zio.mock.*
import domain.{Ingredient, IngredientId}
import domain.DomainError.*
import repo.IngredientRepository

object IngredientRepoMock extends Mock[IngredientRepository]:
  object Add     extends Effect[Ingredient, Nothing, IngredientId]
  object Delete  extends Effect[IngredientId, Nothing, Unit]
  object GetAll  extends Effect[Unit, Nothing, List[Ingredient]]
  object GetById extends Effect[IngredientId, Nothing, Option[Ingredient]]
  object Update  extends Effect[Ingredient, Nothing, Unit]

  val compose: URLayer[Proxy, IngredientRepository] =
    ZLayer.fromFunction { (proxy: Proxy) =>
      new IngredientRepository {
        override def add(ingredient: Ingredient) = proxy(Add, ingredient)

        override def delete(id: IngredientId) = proxy(Delete, id)

        override def getAll() = proxy(GetAll)

        override def getById(id: IngredientId) = proxy(GetById, id)

        override def update(ingredient: Ingredient) = proxy(Update, ingredient)
      }
    }
