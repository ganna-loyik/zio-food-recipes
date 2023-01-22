package repo

import zio.*
import domain.*

final class InMemoryRecipeRepository(
  random: Random,
  dataRef: Ref[Map[Long, Recipe]]
) extends RecipeRepository:

  def add(description: String): IO[RepositoryError, Long] =
    for {
      id <- random.nextLong.map(_.abs)
      _  <- dataRef.update(map => map + (id -> Recipe(id, description)))
    } yield id

  def delete(id: Long): IO[RepositoryError, Unit] =
    dataRef.update(map => map - id)

  def getAll(): IO[RepositoryError, List[Recipe]] =
    for {
      recipesMap <- dataRef.get
    } yield recipesMap.view.values.toList

  def getById(id: Long): IO[RepositoryError, Option[Recipe]] =
    for {
      values <- dataRef.get
    } yield values.get(id)

  def getByIds(ids: Set[Long]): IO[RepositoryError, List[Recipe]] =
    for {
      values <- dataRef.get
    } yield values.filter(id => ids.contains(id._1)).view.values.toList

  def update(recipe: Recipe): IO[RepositoryError, Unit] =
    dataRef.update(map => map + (recipe.id -> recipe))

object InMemoryRecipeRepository:
  val layer: ZLayer[Random, Nothing, RecipeRepository] =
    ZLayer(for {
      random  <- ZIO.service[Random]
      dataRef <- Ref.make(Map.empty[Long, Recipe])
    } yield InMemoryRecipeRepository(random, dataRef))
