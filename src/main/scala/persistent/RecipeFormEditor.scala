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
        emptyState = RecipeFormOpenState.empty,
        (state, command) => commandHandler(recipeFormId, state, command),
        eventHandler
      )
    }
  }

  private def commandHandlerForSavedRecipeForm(
    recipeFormId: String,
    state: RecipeFormSavedState,
    command: RecipeFormEditorCommand
  ): Effect[RecipeFormEditorEvent, RecipeFormEditorState] =
    command match {
      case Get(replyTo)  =>
        replyTo ! state.toSummary
        Effect.none
      case Save(replyTo) =>
        Effect.unhandled.thenRun(_ =>
          replyTo ! StatusReply.Error(s"Can't save already saved $recipeFormId recipe form")
        )
      case _             => Effect.none
    }

  private def commandHandlerForOpenRecipeForm(
    recipeFormId: String,
    state: RecipeFormOpenState,
    command: RecipeFormEditorCommand
  ): Effect[RecipeFormEditorEvent, RecipeFormEditorState] =
    command match {
      case UpdateName(name, replyTo) =>
        Effect.persist(NameUpdated(recipeFormId, name)).thenRun(_ => replyTo ! Done)

      case UpdateDescription(description, replyTo) =>
        Effect.persist(DescriptionUpdated(recipeFormId, description)).thenRun(_ => replyTo ! Done)

      case UpdateInstructions(instructions, replyTo) =>
        Effect.persist(InstructionsUpdated(recipeFormId, instructions)).thenRun(_ => replyTo ! Done)

      case UpdatePreparationTime(minutes, replyTo) =>
        Effect.persist(PreparationTimeUpdated(recipeFormId, minutes)).thenRun(_ => replyTo ! Done)

      case UpdateWaitingTime(minutes, replyTo) =>
        Effect.persist(WaitingTimeUpdated(recipeFormId, minutes)).thenRun(_ => replyTo ! Done)

      case AddIngridient(ingridient, amount, unit, replyTo) =>
        if (state.hasIngridient(ingridient))
          Effect.unhandled.thenRun(_ =>
            replyTo ! StatusReply.Error(s"Ingridient '$ingridient' was already added to this recipe form")
          )
        else if (amount <= 0)
          Effect.unhandled.thenRun(_ => replyTo ! StatusReply.Error(s"Amount must be greater than zero"))
        else
          Effect
            .persist(IngridientAdded(recipeFormId, ingridient, amount, unit))
            .thenRun(_ => replyTo ! StatusReply.success(Done))

      case RemoveIngridient(ingridient, replyTo) =>
        if (state.hasIngridient(ingridient))
          Effect.persist(IngridientRemoved(recipeFormId, ingridient)).thenRun(_ => replyTo ! StatusReply.success(Done))
        else Effect.none

      case AdjustIngridientAmount(ingridient, amount, unit, replyTo) =>
        if (amount <= 0)
          Effect.unhandled.thenRun(_ => replyTo ! StatusReply.Error(s"Amount must be greater than zero"))
        else if (state.hasIngridient(ingridient))
          Effect
            .persist(IngridientAmountAdjusted(recipeFormId, ingridient, amount, unit))
            .thenRun(_ => replyTo ! StatusReply.success(Done))
        else
          Effect.unhandled.thenRun(_ =>
            replyTo ! StatusReply.Error(s"Cannot adjust amount for ingridient '$ingridient' as it isn't in recipe form")
          )

      case AddTag(tag, replyTo) =>
        if (state.hasTag(tag))
          Effect.unhandled.thenRun(_ =>
            replyTo ! StatusReply.Error(s"Tag '$tag' was already added to this recipe form")
          )
        else
          Effect.persist(TagAdded(recipeFormId, tag)).thenRun(_ => replyTo ! StatusReply.success(Done))

      case RemoveTag(tag, replyTo) =>
        if (state.hasTag(tag))
          Effect.persist(TagRemoved(recipeFormId, tag)).thenRun(_ => replyTo ! StatusReply.success(Done))
        else Effect.none

      case Save(replyTo) =>
        if (!state.isCompleted)
          Effect.unhandled.thenRun(_ => replyTo ! StatusReply.Error("Cannot save not completed recipe form"))
        else
          Effect.persist(Saved(recipeFormId)).thenRun(_ => replyTo ! StatusReply.success(Done))
      case Get(replyTo)  =>
        replyTo ! state.toSummary
        Effect.none
    }

  private def commandHandler(
    recipeFormId: String,
    state: RecipeFormEditorState,
    command: RecipeFormEditorCommand
  ): Effect[RecipeFormEditorEvent, RecipeFormEditorState] =
    state match
      case openState: RecipeFormOpenState   => commandHandlerForOpenRecipeForm(recipeFormId, openState, command)
      case savedState: RecipeFormSavedState => commandHandlerForSavedRecipeForm(recipeFormId, savedState, command)

  private def eventHandler(state: RecipeFormEditorState, event: RecipeFormEditorEvent): RecipeFormEditorState = {
    state match
      case state: RecipeFormOpenState  =>
        event match {
          case NameUpdated(_, name)                                  => state.updateName(name)
          case DescriptionUpdated(_, description)                    => state.updateDescription(description)
          case InstructionsUpdated(_, instructions)                  => state.updateInstructions(instructions)
          case PreparationTimeUpdated(_, minutes)                    => state.updatePrepationTime(minutes)
          case WaitingTimeUpdated(_, minutes)                        => state.updateWaitingTime(minutes)
          case IngridientAdded(_, ingridient, amount, unit)          => state.updateIngridient(ingridient, amount, unit)
          case IngridientRemoved(_, ingridient)                      => state.removeIngridient(ingridient)
          case IngridientAmountAdjusted(_, ingridient, amount, unit) => state.updateIngridient(ingridient, amount, unit)
          case TagAdded(_, tag)                                      => state.addTag(tag)
          case TagRemoved(_, tag)                                    => state.removeTag(tag)
          case Saved(_)                                              => RecipeFormSavedState(state.form)
        }
      case state: RecipeFormSavedState =>
        throw new IllegalStateException(s"unexpected event [$event] in state [$state]")

  }
}
