package healthcheck

import zio.*
import zhttp.http.*

object Healthcheck:

  val routes: HttpApp[Any, Throwable] = Http.collect { case Method.GET -> !! / "health" =>
    Response.status(Status.NoContent)
  }
