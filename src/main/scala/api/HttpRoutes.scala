package api

import zio.*
import zio.http.*
import zio.http.model.*
import zio.json.*
import zio.stream.ZStream
import domain.{DomainError, Recipe, RecipeId}
import protocol.*
import service.RecipeService

import java.nio.charset.StandardCharsets

object HttpRoutes:

  val app: HttpApp[RecipeService, Throwable] =
    Http.collectRoute[Request] { case Method.GET -> !! / "graphql" =>
      Handler.fromStream(ZStream.fromResource("graphiql.html")).toHttp
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
