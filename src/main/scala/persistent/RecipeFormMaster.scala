package persistent

import akka.Done
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.pattern.StatusReply
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior}
import java.util.UUID
import zio.{UIO, ZIO}

object RecipeFormMaster {
  def apply(): Behavior[RecipeFormEditorCommand] = {
    Behaviors.setup { context =>
      context.log.info("Creating recipe form master")
      EventSourcedBehavior[RecipeFormEditorCommand, RecipeFormMasterEvent, RecipeFormMasterState](
        PersistenceId.ofUniqueId("recipeFormMaster"),
        emptyState = RecipeFormMasterState(Map()),
        commandHandler(context),
        eventHandler(context)
      )
    }
  }

  private def commandHandler(
    context: ActorContext[RecipeFormEditorCommand]
  ): (RecipeFormMasterState, RecipeFormEditorCommand) => Effect[RecipeFormMasterEvent, RecipeFormMasterState] =
    (state, command) =>
      command match {
        case command: Create =>
          val id = UUID.randomUUID().toString
          val editor = context.spawn(RecipeFormEditor(id), id)
          Effect.persist(RecipeFormEditorCreated(id)).thenReply(editor)(_ => command)

        case command: UpdateCommand =>
          state.editors.get(command.id) match {
            case Some(editor) =>
              Effect.reply(editor)(command)
            case None         =>
              Effect.reply(command.replyTo)(
                DoneResponse(StatusReply.error(s"Recipe form editor ${command.id} cannot be found"))
              )
          }
          
        case Get(id, replyTo)       =>
          state.editors.get(id) match {
            case Some(editor) =>
              Effect.reply(editor)(command)
            case None         =>
              Effect.reply(replyTo)(
                GetResponse(StatusReply.error(s"Recipe form editor $id cannot be found"))
              )
          }

        case Save(id, replyTo) =>
          state.editors.get(id) match {
            case Some(editor) =>
              Effect.reply(editor)(command)
            case None         =>
              Effect.reply(replyTo)(
                DoneResponse(StatusReply.error(s"Recipe form editor $id cannot be found"))
              )
          }
      }

  private def eventHandler(
    context: ActorContext[RecipeFormEditorCommand]
  ): (RecipeFormMasterState, RecipeFormMasterEvent) => RecipeFormMasterState = (state, event) =>
    event match {
      case RecipeFormEditorCreated(id) =>
        val editorRef = context.child(id).getOrElse(context.spawn(RecipeFormEditor(id), id))
        state.addEditor(id, editorRef.asInstanceOf[ActorRef[RecipeFormEditorCommand]])
    }
}
