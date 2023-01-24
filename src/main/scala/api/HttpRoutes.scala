package api

import zhttp.http.*
import zhttp.service.*
import zio.*
import zio.json.*
import domain.{DomainError, RecipeId}
import protocol.*
import service.RecipeService
import service.RecipeService.*

import java.nio.charset.StandardCharsets

object HttpRoutes:

  val app: HttpApp[RecipeService, Nothing] = Http.collectZIO {
    case Method.GET -> !! / "recipes" =>
      getAllRecipes().map(recipes =>
        Response.json(
          GetRecipes(recipes.map(recipe => GetRecipe(recipe.id.value, recipe.name, recipe.description))).toJson
        )
      )

    case Method.GET -> !! / "recipes" / id =>
      getRecipeById(RecipeId(id.toLong))
        .map {
          case Some(recipe) => Response.json(GetRecipe(recipe.id.value, recipe.name, recipe.description).toJson)
          case None         => Response.status(Status.NotFound)
        }

    case Method.DELETE -> !! / "recipes" / id =>
      deleteRecipe(RecipeId(id.toLong)).map(_ => Response.ok)

    case req @ Method.POST -> !! / "recipes" =>
      (for
        body <- entity[CreateRecipe](req).absolve
          .tapError(_ => ZIO.logInfo(s"Unparseable body"))
        id   <- addRecipe(body.name, body.description)
      yield GetRecipe(id.value, body.name, body.description)).either.map {
        case Right(created) =>
          Response(
            Status.Created,
            Headers(HeaderNames.contentType, HeaderValues.applicationJson),
            HttpData.fromString(created.toJson)
          )
        case Left(_)        => Response.status(Status.BadRequest)
      }

    case req @ Method.PUT -> !! / "recipes" / id =>
      (for
        update <- entity[UpdateRecipe](req).absolve
          .tapError(_ => ZIO.logInfo(s"Unparseable body "))
        _      <- updateRecipe(RecipeId(id.toLong), update.name, update.description)
      yield ()).either.map {
        case Left(_)  => Response.status(Status.BadRequest)
        case Right(_) => Response.ok
      }
  }

  private def entity[T: JsonDecoder](req: Request): ZIO[Any, Throwable, Either[String, T]] =
    req.data.toByteBuf.map { byteBuf =>
      val bytes = Array[Byte]()
      byteBuf.readBytes(bytes)
      new String(bytes, StandardCharsets.UTF_8).fromJson[T]
    }
