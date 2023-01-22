package domain

case class Recipe(id: Long, description: String)

enum DomainError(val msg: String):
  case BusinessError(message: String) extends DomainError(message)
