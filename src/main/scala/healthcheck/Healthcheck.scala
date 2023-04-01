package healthcheck

import zio.*
import zio.http.*
import zio.http.model.*

object Healthcheck:

  val routes: HttpApp[Any, Throwable] = Http.collect { case Method.GET -> !! / "health" =>
    Response.status(Status.NoContent)
  }
