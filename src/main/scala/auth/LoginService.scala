package auth

import zio.{URIO, ZIO}

trait LoginService:
  def login(info: LoginInfo): Option[JWT]

object LoginService:
  def login(info: LoginInfo): URIO[LoginService, Option[JWT]] =
    ZIO.serviceWith[LoginService](_.login(info))
