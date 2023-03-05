package repo

import akka.Done
import akka.persistence.testkit.scaladsl.EventSourcedBehaviorTestKit
import akka.persistence.typed.PersistenceId
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, ActorTestKitBase}
import akka.actor.testkit.typed.scaladsl.LogCapturing
import akka.pattern.StatusReply
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import domain.*
import persistent.common.*
import persistent.editor.*
import persistent.master.*

abstract class ScalaTestWithActorTestKit(testKit: ActorTestKit) extends ActorTestKitBase(testKit) {
  def this(config: Config) = this(ActorTestKit(ActorTestKitBase.testNameFromCallStack(), config))
  def afterAll(): Unit = ()
}

class RecipeFormEditorLiveSpec
  extends ScalaTestWithActorTestKit(
    EventSourcedBehaviorTestKit.config
      .withFallback(ConfigFactory.load())
  )
     with AnyWordSpecLike
     with Matchers
     with BeforeAndAfterEach {

  private val eventSourcedTestKit =
    EventSourcedBehaviorTestKit[RecipeFormEditorCommand, RecipeFormEditorEvent, RecipeFormMasterState](
      system,
      RecipeFormMaster()
    )

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    eventSourcedTestKit.clear()
  }

  "Recipe editor form" must {
    "be created" in {
      val result = eventSourcedTestKit.runCommand[RecipeFormEditorResponse](Create(_))
      val editorId = result.reply.asInstanceOf[CreatedResponse].id.toOption.get
      result.event shouldBe RecipeFormEditorCreated(editorId)
      result.reply shouldBe CreatedResponse(Right(editorId))
      result.state.editors.keySet.contains(editorId) shouldBe true
    }

    "be updated and saved" in {
      val result = eventSourcedTestKit.runCommand[RecipeFormEditorResponse](Create(_))
      val id = result.reply.asInstanceOf[CreatedResponse].id.toOption.get
      eventSourcedTestKit.runCommand[RecipeFormEditorResponse](ref => UpdateName(id, "test", ref))
      eventSourcedTestKit.runCommand[RecipeFormEditorResponse](ref => UpdateDescription(id, "testDescr", ref))
      eventSourcedTestKit.runCommand[RecipeFormEditorResponse](ref => UpdateInstructions(id, "testInstr", ref))
      eventSourcedTestKit.runCommand[RecipeFormEditorResponse](ref => UpdatePreparationTime(id, 10, ref))
      eventSourcedTestKit.runCommand[RecipeFormEditorResponse](ref => UpdateWaitingTime(id, 30, ref))
      eventSourcedTestKit.runCommand[RecipeFormEditorResponse](ref =>
        AddIngredient(id, "apple", 100, IngredientUnit.Gram.toString, ref)
      )
      eventSourcedTestKit.runCommand[RecipeFormEditorResponse](ref => AddTag(id, "testTag", ref))
      val resultSaved = eventSourcedTestKit.runCommand[RecipeFormEditorResponse](ref => Save(id, ref))
      resultSaved.reply shouldBe DoneResponse(Right(true))
    }

    "throw error when saving non-completed form" in {
      val result = eventSourcedTestKit.runCommand[RecipeFormEditorResponse](Create(_))
      val id = result.reply.asInstanceOf[CreatedResponse].id.toOption.get
      eventSourcedTestKit.runCommand[RecipeFormEditorResponse](ref => UpdateName(id, "test", ref))
      val resultSaved = eventSourcedTestKit.runCommand[RecipeFormEditorResponse](ref => Save(id, ref))
      resultSaved.reply.asInstanceOf[DoneResponse].done.isLeft shouldBe true
    }
  }
}
