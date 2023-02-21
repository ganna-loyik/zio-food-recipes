package auth

import zhttp.http.middleware.HttpMiddleware
import zhttp.http.Middleware.bearerAuthZIO
import zio.ZIO

object AuthMiddleware:
  val middleware: HttpMiddleware[JwtDecoder, Nothing] =
    bearerAuthZIO(token => ZIO.serviceWith[JwtDecoder](_.decode(token).isDefined))
