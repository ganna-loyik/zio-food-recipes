package domain

enum DomainError(val msg: String):
  case BusinessError(message: String) extends DomainError(message)