package auth

import zio.{URLayer, ZLayer}

case class LoginServiceLive(encoder: JwtEncoder[AuthInfo]) extends LoginService:
  // Now this is just a mock
  private def validate(username: String, password: String): Boolean =
    password == "test"

  override def login(info: LoginInfo): Option[JWT] = {
    if (validate(info.username, info.password)) Some(encoder.encode(AuthInfo(info.username))) else None
  }

object LoginServiceLive:
  lazy val layer: URLayer[JwtEncoder[AuthInfo], LoginService] =
    ZLayer.fromFunction(encoder => LoginServiceLive(encoder))
