package repo

import zio.*
import zio.mock.*
import domain.{Recipe, RecipeId}
import domain.DomainError.*
import repo.RecipeRepository

object RecipeRepoMock extends Mock[RecipeRepository]:
  object Add     extends Effect[(String, Option[String]), Nothing, RecipeId]
  object Delete  extends Effect[RecipeId, Nothing, Unit]
  object GetAll  extends Effect[Unit, Nothing, List[Recipe]]
  object GetById extends Effect[RecipeId, Nothing, Option[Recipe]]
  object Update  extends Effect[Recipe, Nothing, Unit]

  val compose: URLayer[Proxy, RecipeRepository] =
    ZLayer.fromFunction { (proxy: Proxy) =>
      new RecipeRepository {
        override def add(name: String, description: Option[String]) = proxy(Add, name, description)

        override def delete(id: RecipeId) = proxy(Delete, id)

        override def getAll() = proxy(GetAll)

        override def getById(id: RecipeId) = proxy(GetById, id)

        override def update(recipe: Recipe) = proxy(Update, recipe)
      }
    }
