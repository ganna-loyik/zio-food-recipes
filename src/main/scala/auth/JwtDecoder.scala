package auth

import pdi.jwt.JwtClaim

trait JwtDecoder:
  def decode(token: JWT): Option[JwtClaim]
