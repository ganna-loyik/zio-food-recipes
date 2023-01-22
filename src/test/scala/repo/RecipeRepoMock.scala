package repo

import zio.*
import zio.mock.*
import domain.Recipe
import domain.DomainError.*
import repo.RecipeRepository

object RecipeRepoMock extends Mock[RecipeRepository]:
  object Add extends Effect[String, Nothing, Long]
  object Delete extends Effect[Long, Nothing, Unit]
  object GetAll extends Effect[Unit, Nothing, List[Recipe]]
  object GetById extends Effect[Long, Nothing, Option[Recipe]]
  object Update extends Effect[Recipe, Nothing, Unit]

  val compose: URLayer[Proxy, RecipeRepository] =
    ZLayer.fromFunction { (proxy: Proxy) =>
      new RecipeRepository {
        override def add(description: String) = proxy(Add, description)

        override def delete(id: Long) = proxy(Delete, id)

        override def getAll() = proxy(GetAll)

        override def getById(id: Long) = proxy(GetById, id)

        override def update(recipe: Recipe) = proxy(Update, recipe)
      }
    }
