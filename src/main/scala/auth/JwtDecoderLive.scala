package auth

import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import zio.{ULayer, ZLayer}

case class JwtDecoderLive() extends JwtDecoder:
  private val SECRET_KEY = "secretKey"
  private val jwtAlgorithm = JwtAlgorithm.HS512

  override def decode(token: JWT): Option[JwtClaim] = {
    Jwt.decode(token, SECRET_KEY, Seq(jwtAlgorithm)).toOption
  }

object JwtDecoderLive:
  val layer: ULayer[JwtDecoder] = ZLayer.succeed(JwtDecoderLive())
