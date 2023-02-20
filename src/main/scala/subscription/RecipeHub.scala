package subscription

import domain.Recipe
import zio.*
import zio.stream.{UStream, ZStream}

trait RecipeHub:
  def publish(recipe: Recipe): UIO[Unit]

  def subscribe(): UStream[Recipe]

object RecipeHub:
  def publishRecipe(recipe: Recipe): URIO[RecipeHub, Unit] =
    ZIO.serviceWithZIO[RecipeHub](_.publish(recipe))

  def subscribe(): ZStream[RecipeHub, Nothing, Recipe] =
    ZStream.serviceWithStream[RecipeHub](_.subscribe())

case class RecipeHubLive(hub: Hub[Recipe]) extends RecipeHub {
  def publish(recipe: Recipe) = hub.publish(recipe).unit

  def subscribe() = ZStream.fromHub(hub)
}

object RecipeHubLive:
  val layer: ULayer[RecipeHubLive] =
    ZLayer(Hub.sliding[Recipe](8).map(RecipeHubLive(_)))
