package graphql.types.editor

import domain.IngridientUnit

case class UpdateIngridientInput(id: String, ingridient: String, amount: Int, unit: IngridientUnit)
