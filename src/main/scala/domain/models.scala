package domain

case class RecipeId(value: Long) extends AnyVal

case class Recipe(id: RecipeId, name: String, description: Option[String])

enum DomainError(val msg: String):
  case BusinessError(message: String) extends DomainError(message)
