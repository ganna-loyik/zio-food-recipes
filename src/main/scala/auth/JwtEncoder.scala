package auth

trait JwtEncoder[A]:
  def encode(info: A): JWT