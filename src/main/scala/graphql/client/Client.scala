package graphql.client

import caliban.client.CalibanClientError.DecodingError
import caliban.client.FieldBuilder._
import caliban.client._
import caliban.client.__Value._

object Client {

  sealed trait IngredientUnit extends scala.Product with scala.Serializable { def value: String }
  object IngredientUnit {
    case object Gram       extends IngredientUnit { val value: String = "Gram"       }
    case object Milliliter extends IngredientUnit { val value: String = "Milliliter" }

    implicit val decoder: ScalarDecoder[IngredientUnit] = {
      case __StringValue("Gram")       => Right(IngredientUnit.Gram)
      case __StringValue("Milliliter") => Right(IngredientUnit.Milliliter)
      case other                       => Left(DecodingError(s"Can't build IngredientUnit from input $other"))
    }
    implicit val encoder: ArgEncoder[IngredientUnit] = {
      case IngredientUnit.Gram       => __EnumValue("Gram")
      case IngredientUnit.Milliliter => __EnumValue("Milliliter")
    }

    val values: scala.collection.immutable.Vector[IngredientUnit] = scala.collection.immutable.Vector(Gram, Milliliter)
  }

  sealed trait RecipeSorting extends scala.Product with scala.Serializable { def value: String }
  object RecipeSorting {
    case object Default         extends RecipeSorting { val value: String = "Default"         }
    case object Name            extends RecipeSorting { val value: String = "Name"            }
    case object PreparationTime extends RecipeSorting { val value: String = "PreparationTime" }

    implicit val decoder: ScalarDecoder[RecipeSorting] = {
      case __StringValue("Default")         => Right(RecipeSorting.Default)
      case __StringValue("Name")            => Right(RecipeSorting.Name)
      case __StringValue("PreparationTime") => Right(RecipeSorting.PreparationTime)
      case other                            => Left(DecodingError(s"Can't build RecipeSorting from input $other"))
    }
    implicit val encoder: ArgEncoder[RecipeSorting] = {
      case RecipeSorting.Default         => __EnumValue("Default")
      case RecipeSorting.Name            => __EnumValue("Name")
      case RecipeSorting.PreparationTime => __EnumValue("PreparationTime")
    }

    val values: scala.collection.immutable.Vector[RecipeSorting] =
      scala.collection.immutable.Vector(Default, Name, PreparationTime)
  }

  sealed trait SortingOrder extends scala.Product with scala.Serializable { def value: String }
  object SortingOrder {
    case object Ascending  extends SortingOrder { val value: String = "Ascending"  }
    case object Descending extends SortingOrder { val value: String = "Descending" }

    implicit val decoder: ScalarDecoder[SortingOrder] = {
      case __StringValue("Ascending")  => Right(SortingOrder.Ascending)
      case __StringValue("Descending") => Right(SortingOrder.Descending)
      case other                       => Left(DecodingError(s"Can't build SortingOrder from input $other"))
    }
    implicit val encoder: ArgEncoder[SortingOrder] = {
      case SortingOrder.Ascending  => __EnumValue("Ascending")
      case SortingOrder.Descending => __EnumValue("Descending")
    }

    val values: scala.collection.immutable.Vector[SortingOrder] =
      scala.collection.immutable.Vector(Ascending, Descending)
  }

  type Ingredient
  object Ingredient {
    def id: SelectionBuilder[Ingredient, Long] = _root_.caliban.client.SelectionBuilder.Field("id", Scalar())
    def name: SelectionBuilder[Ingredient, String] = _root_.caliban.client.SelectionBuilder.Field("name", Scalar())
  }

  type KVStringTupleIntAndString
  object KVStringTupleIntAndString {

    /**
 * Key
 */
    def key: SelectionBuilder[KVStringTupleIntAndString, String] =
      _root_.caliban.client.SelectionBuilder.Field("key", Scalar())

    /**
 * Value
 */
    def value[A](
      innerSelection: SelectionBuilder[TupleIntAndString, A]
    ): SelectionBuilder[KVStringTupleIntAndString, A] =
      _root_.caliban.client.SelectionBuilder.Field("value", Obj(innerSelection))
  }

  type Recipe
  object Recipe {
    def id: SelectionBuilder[Recipe, Long] = _root_.caliban.client.SelectionBuilder.Field("id", Scalar())
    def name: SelectionBuilder[Recipe, String] = _root_.caliban.client.SelectionBuilder.Field("name", Scalar())
    def description: SelectionBuilder[Recipe, scala.Option[String]] =
      _root_.caliban.client.SelectionBuilder.Field("description", OptionOf(Scalar()))
    def instructions: SelectionBuilder[Recipe, String] =
      _root_.caliban.client.SelectionBuilder.Field("instructions", Scalar())
    def preparationTimeMinutes: SelectionBuilder[Recipe, Int] =
      _root_.caliban.client.SelectionBuilder.Field("preparationTimeMinutes", Scalar())
    def waitingTimeMinutes: SelectionBuilder[Recipe, Int] =
      _root_.caliban.client.SelectionBuilder.Field("waitingTimeMinutes", Scalar())
    def time: SelectionBuilder[Recipe, String] = _root_.caliban.client.SelectionBuilder.Field("time", Scalar())
    def tags: SelectionBuilder[Recipe, List[String]] =
      _root_.caliban.client.SelectionBuilder.Field("tags", ListOf(Scalar()))
    def ingredients: SelectionBuilder[Recipe, String] =
      _root_.caliban.client.SelectionBuilder.Field("ingredients", Scalar())
  }

  type RecipeForm
  object RecipeForm {
    def name: SelectionBuilder[RecipeForm, scala.Option[String]] =
      _root_.caliban.client.SelectionBuilder.Field("name", OptionOf(Scalar()))
    def description: SelectionBuilder[RecipeForm, scala.Option[String]] =
      _root_.caliban.client.SelectionBuilder.Field("description", OptionOf(Scalar()))
    def instructions: SelectionBuilder[RecipeForm, scala.Option[String]] =
      _root_.caliban.client.SelectionBuilder.Field("instructions", OptionOf(Scalar()))
    def preparationTimeMinutes: SelectionBuilder[RecipeForm, scala.Option[Int]] =
      _root_.caliban.client.SelectionBuilder.Field("preparationTimeMinutes", OptionOf(Scalar()))
    def waitingTimeMinutes: SelectionBuilder[RecipeForm, scala.Option[Int]] =
      _root_.caliban.client.SelectionBuilder.Field("waitingTimeMinutes", OptionOf(Scalar()))
    def tags: SelectionBuilder[RecipeForm, List[String]] =
      _root_.caliban.client.SelectionBuilder.Field("tags", ListOf(Scalar()))
    def ingredients[A](
      innerSelection: SelectionBuilder[KVStringTupleIntAndString, A]
    ): SelectionBuilder[RecipeForm, List[A]] =
      _root_.caliban.client.SelectionBuilder.Field("ingredients", ListOf(Obj(innerSelection)))
  }

  type RecipeTag
  object RecipeTag {
    def name: SelectionBuilder[RecipeTag, String] = _root_.caliban.client.SelectionBuilder.Field("name", Scalar())
  }

  type Summary
  object Summary {
    def form[A](innerSelection: SelectionBuilder[RecipeForm, A]): SelectionBuilder[Summary, A] =
      _root_.caliban.client.SelectionBuilder.Field("form", Obj(innerSelection))
    def isSaved: SelectionBuilder[Summary, Boolean] = _root_.caliban.client.SelectionBuilder.Field("isSaved", Scalar())
  }

  type TupleIntAndString
  object TupleIntAndString {

    /**
 * First element of the tuple
 */
    def _1: SelectionBuilder[TupleIntAndString, Int] = _root_.caliban.client.SelectionBuilder.Field("_1", Scalar())

    /**
 * Second element of the tuple
 */
    def _2: SelectionBuilder[TupleIntAndString, String] = _root_.caliban.client.SelectionBuilder.Field("_2", Scalar())
  }

  final case class IngredientInputInput(name: String, amount: Int, unit: IngredientUnit)
  object IngredientInputInput {
    implicit val encoder: ArgEncoder[IngredientInputInput] = new ArgEncoder[IngredientInputInput] {
      override def encode(value: IngredientInputInput): __Value =
        __ObjectValue(
          List(
            "name"   -> implicitly[ArgEncoder[String]].encode(value.name),
            "amount" -> implicitly[ArgEncoder[Int]].encode(value.amount),
            "unit"   -> implicitly[ArgEncoder[IngredientUnit]].encode(value.unit)
          )
        )
    }
  }
  final case class RecipeFiltersInput(
    name: scala.Option[String] = None,
    preparationTimeTo: scala.Option[Int] = None,
    waitingTimeTo: scala.Option[Int] = None,
    tags: List[String] = Nil,
    ingredients: List[String] = Nil
  )
  object RecipeFiltersInput   {
    implicit val encoder: ArgEncoder[RecipeFiltersInput] = new ArgEncoder[RecipeFiltersInput] {
      override def encode(value: RecipeFiltersInput): __Value =
        __ObjectValue(
          List(
            "name" -> value.name.fold(__NullValue: __Value)(value => implicitly[ArgEncoder[String]].encode(value)),
            "preparationTimeTo" -> value.preparationTimeTo.fold(__NullValue: __Value)(value =>
              implicitly[ArgEncoder[Int]].encode(value)
            ),
            "waitingTimeTo"     -> value.waitingTimeTo.fold(__NullValue: __Value)(value =>
              implicitly[ArgEncoder[Int]].encode(value)
            ),
            "tags"              -> __ListValue(value.tags.map(value => implicitly[ArgEncoder[String]].encode(value))),
            "ingredients" -> __ListValue(value.ingredients.map(value => implicitly[ArgEncoder[String]].encode(value)))
          )
        )
    }
  }
  type Queries = _root_.caliban.client.Operations.RootQuery
  object Queries              {
    def recipe[A](id: Long)(innerSelection: SelectionBuilder[Recipe, A])(implicit
      encoder0: ArgEncoder[Long]
    ): SelectionBuilder[_root_.caliban.client.Operations.RootQuery, scala.Option[A]] =
      _root_.caliban.client.SelectionBuilder
        .Field("recipe", OptionOf(Obj(innerSelection)), arguments = List(Argument("id", id, "Long!")(encoder0)))
    def recipes[A](
      filters: scala.Option[RecipeFiltersInput] = None,
      sorting: RecipeSorting,
      sortingOrder: SortingOrder
    )(innerSelection: SelectionBuilder[Recipe, A])(implicit
      encoder0: ArgEncoder[scala.Option[RecipeFiltersInput]],
      encoder1: ArgEncoder[RecipeSorting],
      encoder2: ArgEncoder[SortingOrder]
    ): SelectionBuilder[_root_.caliban.client.Operations.RootQuery, List[A]] =
      _root_.caliban.client.SelectionBuilder.Field(
        "recipes",
        ListOf(Obj(innerSelection)),
        arguments = List(
          Argument("filters", filters, "RecipeFiltersInput")(encoder0),
          Argument("sorting", sorting, "RecipeSorting!")(encoder1),
          Argument("sortingOrder", sortingOrder, "SortingOrder!")(encoder2)
        )
      )
    def recipeTags[A](
      innerSelection: SelectionBuilder[RecipeTag, A]
    ): SelectionBuilder[_root_.caliban.client.Operations.RootQuery, List[A]] =
      _root_.caliban.client.SelectionBuilder.Field("recipeTags", ListOf(Obj(innerSelection)))
    def ingredients[A](
      innerSelection: SelectionBuilder[Ingredient, A]
    ): SelectionBuilder[_root_.caliban.client.Operations.RootQuery, List[A]] =
      _root_.caliban.client.SelectionBuilder.Field("ingredients", ListOf(Obj(innerSelection)))
    def getRecipeForm[A](id: String)(innerSelection: SelectionBuilder[Summary, A])(implicit
      encoder0: ArgEncoder[String]
    ): SelectionBuilder[_root_.caliban.client.Operations.RootQuery, scala.Option[A]] =
      _root_.caliban.client.SelectionBuilder.Field(
        "getRecipeForm",
        OptionOf(Obj(innerSelection)),
        arguments = List(Argument("id", id, "String!")(encoder0))
      )
  }

  type Mutations = _root_.caliban.client.Operations.RootMutation
  object Mutations {
    def addRecipe(
      name: String,
      description: scala.Option[String] = None,
      instructions: String,
      preparationTimeMinutes: Int,
      waitingTimeMinutes: Int,
      tags: List[String] = Nil,
      ingredients: List[IngredientInputInput] = Nil
    )(implicit
      encoder0: ArgEncoder[String],
      encoder1: ArgEncoder[scala.Option[String]],
      encoder2: ArgEncoder[String],
      encoder3: ArgEncoder[Int],
      encoder4: ArgEncoder[Int],
      encoder5: ArgEncoder[List[String]],
      encoder6: ArgEncoder[List[IngredientInputInput]]
    ): SelectionBuilder[_root_.caliban.client.Operations.RootMutation, Long] =
      _root_.caliban.client.SelectionBuilder.Field(
        "addRecipe",
        Scalar(),
        arguments = List(
          Argument("name", name, "String!")(encoder0),
          Argument("description", description, "String")(encoder1),
          Argument("instructions", instructions, "String!")(encoder2),
          Argument("preparationTimeMinutes", preparationTimeMinutes, "Int!")(encoder3),
          Argument("waitingTimeMinutes", waitingTimeMinutes, "Int!")(encoder4),
          Argument("tags", tags, "[String!]!")(encoder5),
          Argument("ingredients", ingredients, "[IngredientInputInput!]!")(encoder6)
        )
      )
    def updateRecipe(
      id: Long,
      name: String,
      description: scala.Option[String] = None,
      instructions: String,
      preparationTimeMinutes: Int,
      waitingTimeMinutes: Int
    )(implicit
      encoder0: ArgEncoder[Long],
      encoder1: ArgEncoder[String],
      encoder2: ArgEncoder[scala.Option[String]],
      encoder3: ArgEncoder[String],
      encoder4: ArgEncoder[Int],
      encoder5: ArgEncoder[Int]
    ): SelectionBuilder[_root_.caliban.client.Operations.RootMutation, scala.Option[Unit]] =
      _root_.caliban.client.SelectionBuilder.Field(
        "updateRecipe",
        OptionOf(Scalar()),
        arguments = List(
          Argument("id", id, "Long!")(encoder0),
          Argument("name", name, "String!")(encoder1),
          Argument("description", description, "String")(encoder2),
          Argument("instructions", instructions, "String!")(encoder3),
          Argument("preparationTimeMinutes", preparationTimeMinutes, "Int!")(encoder4),
          Argument("waitingTimeMinutes", waitingTimeMinutes, "Int!")(encoder5)
        )
      )
    def deleteRecipe(id: Long)(implicit
      encoder0: ArgEncoder[Long]
    ): SelectionBuilder[_root_.caliban.client.Operations.RootMutation, Unit] = _root_.caliban.client.SelectionBuilder
      .Field("deleteRecipe", Scalar(), arguments = List(Argument("id", id, "Long!")(encoder0)))
    def addRecipeTag(name: String)(implicit
      encoder0: ArgEncoder[String]
    ): SelectionBuilder[_root_.caliban.client.Operations.RootMutation, Long] = _root_.caliban.client.SelectionBuilder
      .Field("addRecipeTag", Scalar(), arguments = List(Argument("name", name, "String!")(encoder0)))
    def deleteRecipeTag(id: Long)(implicit
      encoder0: ArgEncoder[Long]
    ): SelectionBuilder[_root_.caliban.client.Operations.RootMutation, Unit] = _root_.caliban.client.SelectionBuilder
      .Field("deleteRecipeTag", Scalar(), arguments = List(Argument("id", id, "Long!")(encoder0)))
    def addIngredient(name: String)(implicit
      encoder0: ArgEncoder[String]
    ): SelectionBuilder[_root_.caliban.client.Operations.RootMutation, Long] = _root_.caliban.client.SelectionBuilder
      .Field("addIngredient", Scalar(), arguments = List(Argument("name", name, "String!")(encoder0)))
    def updateIngredient(id: Long, name: String)(implicit
      encoder0: ArgEncoder[Long],
      encoder1: ArgEncoder[String]
    ): SelectionBuilder[_root_.caliban.client.Operations.RootMutation, scala.Option[Unit]] =
      _root_.caliban.client.SelectionBuilder.Field(
        "updateIngredient",
        OptionOf(Scalar()),
        arguments = List(Argument("id", id, "Long!")(encoder0), Argument("name", name, "String!")(encoder1))
      )
    def deleteIngredient(id: Long)(implicit
      encoder0: ArgEncoder[Long]
    ): SelectionBuilder[_root_.caliban.client.Operations.RootMutation, Unit] = _root_.caliban.client.SelectionBuilder
      .Field("deleteIngredient", Scalar(), arguments = List(Argument("id", id, "Long!")(encoder0)))
    def addRecipeForm: SelectionBuilder[_root_.caliban.client.Operations.RootMutation, scala.Option[String]] =
      _root_.caliban.client.SelectionBuilder.Field("addRecipeForm", OptionOf(Scalar()))
    def updateNameInRecipeForm(id: String, name: String)(implicit
      encoder0: ArgEncoder[String],
      encoder1: ArgEncoder[String]
    ): SelectionBuilder[_root_.caliban.client.Operations.RootMutation, scala.Option[Unit]] =
      _root_.caliban.client.SelectionBuilder.Field(
        "updateNameInRecipeForm",
        OptionOf(Scalar()),
        arguments = List(Argument("id", id, "String!")(encoder0), Argument("name", name, "String!")(encoder1))
      )
    def updateDescriptionInRecipeForm(id: String, description: String)(implicit
      encoder0: ArgEncoder[String],
      encoder1: ArgEncoder[String]
    ): SelectionBuilder[_root_.caliban.client.Operations.RootMutation, scala.Option[Unit]] =
      _root_.caliban.client.SelectionBuilder.Field(
        "updateDescriptionInRecipeForm",
        OptionOf(Scalar()),
        arguments =
          List(Argument("id", id, "String!")(encoder0), Argument("description", description, "String!")(encoder1))
      )
    def updateInstructionsInRecipeForm(id: String, instructions: String)(implicit
      encoder0: ArgEncoder[String],
      encoder1: ArgEncoder[String]
    ): SelectionBuilder[_root_.caliban.client.Operations.RootMutation, scala.Option[Unit]] =
      _root_.caliban.client.SelectionBuilder.Field(
        "updateInstructionsInRecipeForm",
        OptionOf(Scalar()),
        arguments =
          List(Argument("id", id, "String!")(encoder0), Argument("instructions", instructions, "String!")(encoder1))
      )
    def updatePreparationTimeInRecipeForm(id: String, minutes: Int)(implicit
      encoder0: ArgEncoder[String],
      encoder1: ArgEncoder[Int]
    ): SelectionBuilder[_root_.caliban.client.Operations.RootMutation, scala.Option[Unit]] =
      _root_.caliban.client.SelectionBuilder.Field(
        "updatePreparationTimeInRecipeForm",
        OptionOf(Scalar()),
        arguments = List(Argument("id", id, "String!")(encoder0), Argument("minutes", minutes, "Int!")(encoder1))
      )
    def updateWaitingTimeInRecipeForm(id: String, minutes: Int)(implicit
      encoder0: ArgEncoder[String],
      encoder1: ArgEncoder[Int]
    ): SelectionBuilder[_root_.caliban.client.Operations.RootMutation, scala.Option[Unit]] =
      _root_.caliban.client.SelectionBuilder.Field(
        "updateWaitingTimeInRecipeForm",
        OptionOf(Scalar()),
        arguments = List(Argument("id", id, "String!")(encoder0), Argument("minutes", minutes, "Int!")(encoder1))
      )
    def addIngredientToRecipeForm(id: String, ingredient: String, amount: Int, unit: IngredientUnit)(implicit
      encoder0: ArgEncoder[String],
      encoder1: ArgEncoder[String],
      encoder2: ArgEncoder[Int],
      encoder3: ArgEncoder[IngredientUnit]
    ): SelectionBuilder[_root_.caliban.client.Operations.RootMutation, scala.Option[Unit]] =
      _root_.caliban.client.SelectionBuilder.Field(
        "addIngredientToRecipeForm",
        OptionOf(Scalar()),
        arguments = List(
          Argument("id", id, "String!")(encoder0),
          Argument("ingredient", ingredient, "String!")(encoder1),
          Argument("amount", amount, "Int!")(encoder2),
          Argument("unit", unit, "IngredientUnit!")(encoder3)
        )
      )
    def updateIngredientInRecipeForm(id: String, ingredient: String, amount: Int, unit: IngredientUnit)(implicit
      encoder0: ArgEncoder[String],
      encoder1: ArgEncoder[String],
      encoder2: ArgEncoder[Int],
      encoder3: ArgEncoder[IngredientUnit]
    ): SelectionBuilder[_root_.caliban.client.Operations.RootMutation, scala.Option[Unit]] =
      _root_.caliban.client.SelectionBuilder.Field(
        "updateIngredientInRecipeForm",
        OptionOf(Scalar()),
        arguments = List(
          Argument("id", id, "String!")(encoder0),
          Argument("ingredient", ingredient, "String!")(encoder1),
          Argument("amount", amount, "Int!")(encoder2),
          Argument("unit", unit, "IngredientUnit!")(encoder3)
        )
      )
    def deleteIngredientFromRecipeForm(id: String, ingredient: String)(implicit
      encoder0: ArgEncoder[String],
      encoder1: ArgEncoder[String]
    ): SelectionBuilder[_root_.caliban.client.Operations.RootMutation, scala.Option[Unit]] =
      _root_.caliban.client.SelectionBuilder.Field(
        "deleteIngredientFromRecipeForm",
        OptionOf(Scalar()),
        arguments =
          List(Argument("id", id, "String!")(encoder0), Argument("ingredient", ingredient, "String!")(encoder1))
      )
    def addTagToRecipeForm(id: String, tag: String)(implicit
      encoder0: ArgEncoder[String],
      encoder1: ArgEncoder[String]
    ): SelectionBuilder[_root_.caliban.client.Operations.RootMutation, scala.Option[Unit]] =
      _root_.caliban.client.SelectionBuilder.Field(
        "addTagToRecipeForm",
        OptionOf(Scalar()),
        arguments = List(Argument("id", id, "String!")(encoder0), Argument("tag", tag, "String!")(encoder1))
      )
    def deleteTagFromRecipeForm(id: String, tag: String)(implicit
      encoder0: ArgEncoder[String],
      encoder1: ArgEncoder[String]
    ): SelectionBuilder[_root_.caliban.client.Operations.RootMutation, scala.Option[Unit]] =
      _root_.caliban.client.SelectionBuilder.Field(
        "deleteTagFromRecipeForm",
        OptionOf(Scalar()),
        arguments = List(Argument("id", id, "String!")(encoder0), Argument("tag", tag, "String!")(encoder1))
      )
    def saveRecipeForm(id: String)(implicit
      encoder0: ArgEncoder[String]
    ): SelectionBuilder[_root_.caliban.client.Operations.RootMutation, scala.Option[Unit]] =
      _root_.caliban.client.SelectionBuilder
        .Field("saveRecipeForm", OptionOf(Scalar()), arguments = List(Argument("id", id, "String!")(encoder0)))
  }

  type Subscriptions = _root_.caliban.client.Operations.RootSubscription
  object Subscriptions {
    def newRecipe[A](
      innerSelection: SelectionBuilder[Recipe, A]
    ): SelectionBuilder[_root_.caliban.client.Operations.RootSubscription, A] =
      _root_.caliban.client.SelectionBuilder.Field("newRecipe", Obj(innerSelection))
  }

}

