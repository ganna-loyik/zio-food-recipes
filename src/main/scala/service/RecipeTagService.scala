package service

import zio.*
import domain.{DomainError, RecipeTag, RecipeTagId}

trait RecipeTagService:
  def addRecipeTag(tag: RecipeTag): UIO[RecipeTagId]

  def deleteRecipeTag(id: RecipeTagId): UIO[Unit]

  def getAllRecipeTags(): UIO[List[RecipeTag]]

object RecipeTagService:
  def addRecipeTag(tag: RecipeTag): URIO[RecipeTagService, RecipeTagId] =
    ZIO.serviceWithZIO[RecipeTagService](_.addRecipeTag(tag))

  def deleteRecipeTag(id: RecipeTagId): URIO[RecipeTagService, Unit] =
    ZIO.serviceWithZIO[RecipeTagService](_.deleteRecipeTag(id))

  def getAllRecipeTags(): URIO[RecipeTagService, List[RecipeTag]] =
    ZIO.serviceWithZIO[RecipeTagService](_.getAllRecipeTags())
