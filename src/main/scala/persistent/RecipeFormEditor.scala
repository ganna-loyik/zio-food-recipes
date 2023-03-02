package persistent

import akka.Done
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.pattern.StatusReply
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior}
import zio.{UIO, ZIO}

object RecipeFormEditor {
  def apply(recipeFormId: String): Behavior[RecipeFormEditorCommand] = {
    Behaviors.setup { context =>
      context.log.info("Creating recipe form {}", recipeFormId)
      EventSourcedBehavior[RecipeFormEditorCommand, RecipeFormEditorEvent, RecipeFormEditorState](
        PersistenceId.ofUniqueId(recipeFormId),
        emptyState = RecipeFormOpenState.empty(recipeFormId),
        (state, command) => commandHandler(state, command),
        eventHandler
      )
    }
  }

  private def commandHandlerForSavedRecipeForm(
    state: RecipeFormSavedState,
    command: RecipeFormEditorCommand
  ): Effect[RecipeFormEditorEvent, RecipeFormEditorState] =
    command match {
      case Get(recipeFormId, replyTo)  =>
        Effect.reply(replyTo)(GetResponse(StatusReply.success(state.toSummary)))
      case Save(recipeFormId, replyTo) =>
        Effect.unhandled.thenReply(replyTo)(_ =>
          DoneResponse(StatusReply.Error(s"Can't save already saved $recipeFormId recipe form"))
        )
      case _                           => Effect.none
    }

  private def commandHandlerForOpenRecipeForm(
    state: RecipeFormOpenState,
    command: RecipeFormEditorCommand
  ): Effect[RecipeFormEditorEvent, RecipeFormEditorState] =
    command match {
      case Create(replyTo) =>
        val id = state.id
        Effect.persist(Created(id)).thenReply(replyTo)(_ => CreatedResponse(StatusReply.success(id)))

      case UpdateName(recipeFormId, name, replyTo) =>
        Effect.persist(NameUpdated(recipeFormId, name)).thenReply(replyTo)(_ => DoneResponse(StatusReply.ack))

      case UpdateDescription(recipeFormId, description, replyTo) =>
        Effect
          .persist(DescriptionUpdated(recipeFormId, description))
          .thenReply(replyTo)(_ => DoneResponse(StatusReply.ack))

      case UpdateInstructions(recipeFormId, instructions, replyTo) =>
        Effect
          .persist(InstructionsUpdated(recipeFormId, instructions))
          .thenReply(replyTo)(_ => DoneResponse(StatusReply.ack))

      case UpdatePreparationTime(recipeFormId, minutes, replyTo) =>
        Effect
          .persist(PreparationTimeUpdated(recipeFormId, minutes))
          .thenReply(replyTo)(_ => DoneResponse(StatusReply.ack))

      case UpdateWaitingTime(recipeFormId, minutes, replyTo) =>
        Effect
          .persist(WaitingTimeUpdated(recipeFormId, minutes))
          .thenReply(replyTo)(_ => DoneResponse(StatusReply.ack))

      case AddIngredient(recipeFormId, ingredient, amount, unit, replyTo) =>
        if (state.hasIngredient(ingredient))
          Effect.unhandled.thenReply(replyTo)(_ =>
            DoneResponse(StatusReply.Error(s"Ingredient '$ingredient' was already added to this recipe form"))
          )
        else if (amount <= 0)
          Effect.unhandled.thenReply(replyTo)(_ => DoneResponse(StatusReply.Error(s"Amount must be greater than zero")))
        else
          Effect
            .persist(IngredientAdded(recipeFormId, ingredient, amount, unit))
            .thenReply(replyTo)(_ => DoneResponse(StatusReply.ack))

      case RemoveIngredient(recipeFormId, ingredient, replyTo) =>
        if (state.hasIngredient(ingredient))
          Effect
            .persist(IngredientRemoved(recipeFormId, ingredient))
            .thenReply(replyTo)(_ => DoneResponse(StatusReply.ack))
        else Effect.reply(replyTo)(DoneResponse(StatusReply.error(s"No ingredient $ingredient")))

      case AdjustIngredientAmount(recipeFormId, ingredient, amount, unit, replyTo) =>
        if (amount <= 0)
          Effect.unhandled.thenReply(replyTo)(_ => DoneResponse(StatusReply.Error(s"Amount must be greater than zero")))
        else if (state.hasIngredient(ingredient))
          Effect
            .persist(IngredientAmountAdjusted(recipeFormId, ingredient, amount, unit))
            .thenReply(replyTo)(_ => DoneResponse(StatusReply.ack))
        else
          Effect.unhandled.thenReply(replyTo)(_ =>
            DoneResponse(
              StatusReply.Error(s"Cannot adjust amount for ingredient '$ingredient' as it isn't in recipe form")
            )
          )

      case AddTag(recipeFormId, tag, replyTo) =>
        if (state.hasTag(tag))
          Effect.unhandled.thenReply(replyTo)(_ =>
            DoneResponse(StatusReply.Error(s"Tag '$tag' was already added to this recipe form"))
          )
        else
          Effect.persist(TagAdded(recipeFormId, tag)).thenReply(replyTo)(_ => DoneResponse(StatusReply.ack))

      case RemoveTag(recipeFormId, tag, replyTo) =>
        if (state.hasTag(tag))
          Effect.persist(TagRemoved(recipeFormId, tag)).thenReply(replyTo)(_ => DoneResponse(StatusReply.ack))
        else Effect.reply(replyTo)(DoneResponse(StatusReply.error(s"No tag $tag")))

      case Save(recipeFormId, replyTo) =>
        if (!state.isCompleted)
          Effect.unhandled.thenReply(replyTo)(_ =>
            DoneResponse(StatusReply.Error("Cannot save not completed recipe form"))
          )
        else
          Effect.persist(Saved(recipeFormId)).thenReply(replyTo)(_ => DoneResponse(StatusReply.ack))
      case Get(recipeFormId, replyTo)  =>
        Effect.reply(replyTo)(GetResponse(StatusReply.success(state.toSummary)))
    }

  private def commandHandler(
    state: RecipeFormEditorState,
    command: RecipeFormEditorCommand
  ): Effect[RecipeFormEditorEvent, RecipeFormEditorState] =
    state match
      case openState: RecipeFormOpenState   => commandHandlerForOpenRecipeForm(openState, command)
      case savedState: RecipeFormSavedState => commandHandlerForSavedRecipeForm(savedState, command)

  private def eventHandler(state: RecipeFormEditorState, event: RecipeFormEditorEvent): RecipeFormEditorState = {
    state match
      case state: RecipeFormOpenState  =>
        event match {
          case Created(_)                                            => state
          case NameUpdated(_, name)                                  => state.updateName(name)
          case DescriptionUpdated(_, description)                    => state.updateDescription(description)
          case InstructionsUpdated(_, instructions)                  => state.updateInstructions(instructions)
          case PreparationTimeUpdated(_, minutes)                    => state.updatePrepationTime(minutes)
          case WaitingTimeUpdated(_, minutes)                        => state.updateWaitingTime(minutes)
          case IngredientAdded(_, ingredient, amount, unit)          => state.updateIngredient(ingredient, amount, unit)
          case IngredientRemoved(_, ingredient)                      => state.removeIngredient(ingredient)
          case IngredientAmountAdjusted(_, ingredient, amount, unit) => state.updateIngredient(ingredient, amount, unit)
          case TagAdded(_, tag)                                      => state.addTag(tag)
          case TagRemoved(_, tag)                                    => state.removeTag(tag)
          case Saved(id)                                             => RecipeFormSavedState(id, state.form)
        }
      case state: RecipeFormSavedState =>
        throw new IllegalStateException(s"unexpected event [$event] in state [$state]")

  }
}
