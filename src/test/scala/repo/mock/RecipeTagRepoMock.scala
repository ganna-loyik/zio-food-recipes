package repo.mock

import zio.*
import zio.mock.*
import domain.{RecipeTag, RecipeTagId}
import domain.DomainError.*
import repo.RecipeTagRepository

object RecipeTagRepoMock extends Mock[RecipeTagRepository]:
  object Add     extends Effect[RecipeTag, Nothing, RecipeTagId]
  object Delete  extends Effect[RecipeTagId, Nothing, Unit]
  object GetAll  extends Effect[Unit, Nothing, List[RecipeTag]]

  val compose: URLayer[Proxy, RecipeTagRepository] =
    ZLayer.fromFunction { (proxy: Proxy) =>
      new RecipeTagRepository {
        override def add(recipeTag: RecipeTag) = proxy(Add, recipeTag)

        override def delete(id: RecipeTagId) = proxy(Delete, id)

        override def getAll() = proxy(GetAll)
      }
    }
