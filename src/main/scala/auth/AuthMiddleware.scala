package auth

import zio.http.HttpAppMiddleware.bearerAuthZIO
import zio.http.RequestHandlerMiddleware
import zio.ZIO

object AuthMiddleware:
  val middleware: RequestHandlerMiddleware[Nothing, JwtDecoder, Nothing, Any] =
    bearerAuthZIO(token => ZIO.serviceWith[JwtDecoder](_.decode(token).isDefined))
