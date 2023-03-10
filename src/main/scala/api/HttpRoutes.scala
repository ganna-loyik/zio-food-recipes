package api

import zhttp.http.*
import zhttp.service.*
import zio.*
import zio.json.*
import zio.stream.ZStream
import domain.{DomainError, Recipe, RecipeId}
import protocol.*
import service.RecipeService

import java.nio.charset.StandardCharsets

object HttpRoutes:

  val app: HttpApp[RecipeService, Nothing] =
    Http.collectHttp[Request] { case Method.GET -> !! / "graphql" =>
      Http.fromStream(ZStream.fromResource("graphiql.html"))
    } ++
      Http.collectZIO { case Method.GET -> !! / "recipes" / id =>
        RecipeService
          .getRecipeById(RecipeId(id.toLong))
          .map {
            case Some(recipe) =>
              Response.json(GetRecipe(recipe.id.value, recipe.name, recipe.description, recipe.instructions).toJson)
            case None         => Response.status(Status.NotFound)
          }
      }
