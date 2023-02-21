package auth

import java.time.Clock
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import zio.{ULayer, ZLayer}

case class JwtEncoderLive() extends JwtEncoder[AuthInfo]:
  implicit private val clock: Clock = Clock.systemUTC

  private val SECRET_KEY = "secretKey"
  private val jwtAlgorithm = JwtAlgorithm.HS512

  override def encode(info: AuthInfo): JWT = {
    val json = s"""{"user": "${info.username}"}"""
    val claim = JwtClaim(json).issuedNow.expiresIn(300)
    Jwt.encode(claim, SECRET_KEY, jwtAlgorithm)
  }

object JwtEncoderLive:
  val layer: ULayer[JwtEncoder[AuthInfo]] = ZLayer.succeed(JwtEncoderLive())
