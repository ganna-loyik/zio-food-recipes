package auth

import zhttp.http.*

object AuthRoutes:
  val app: HttpApp[LoginService, Nothing] = Http.collectZIO[Request] {
    case Method.GET -> !! / "login" / username / password =>
      LoginService.login(LoginInfo(username, password)).map {
        case None        => Response.text("Invalid username or password").setStatus(Status.Unauthorized)
        case Some(token) => Response.text(token)
      }
  }
