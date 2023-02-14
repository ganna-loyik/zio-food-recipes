package service

import zio.*
import zio.stream.*
import domain.*
import domain.DomainError.BusinessError
import repo.*

final class RecipeTagServiceLive(repo: RecipeTagRepository) extends RecipeTagService:
  def addRecipeTag(tag: RecipeTag): UIO[RecipeTagId] =
    repo.add(tag).orDie

  def deleteRecipeTag(id: RecipeTagId): UIO[Unit] =
    repo.delete(id).orDie

  def getAllRecipeTags(): UIO[List[RecipeTag]] =
    repo.getAll().orDie

object RecipeTagServiceLive:
  val layer: URLayer[RecipeTagRepository, RecipeTagService] =
    ZLayer(ZIO.service[RecipeTagRepository].map(RecipeTagServiceLive(_)))
