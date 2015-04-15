package actor

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import me.invkrh.cmdchat.actor.{ClientActor, SessionActor}
import me.invkrh.cmdchat.event._
import mock.MockUserInput
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/12/15
 * Time: 12:57 PM
 */

class SessionActorTest extends TestKit(ActorSystem("testSystem"))
with ImplicitSender
with MockUserInput
with FlatSpecLike
with Matchers
with BeforeAndAfterAll {

  val target     = "B"
  val msg        = "this is a msg"
  val privateMsg = s"@$target $msg"

  "SessionActor" should "register client on server side when name is valid" in {
    val sa = TestActorRef(new SessionActor)
    sa ! NameValidation("A", result = true)
    expectMsg(Register("A", sa.getSingleChild("clt_A")))
  }

  "SessionActor" should "retry name check when name is not valid" in {
    cannedInput("B")
    val sa = TestActorRef(new SessionActor)
    sa ! NameValidation("", result = false)
    expectMsg(NameCheck("B"))
  }

  "SessionActor" should "work correctly once Authorized msg is received" in {
    val cltName = "A"
    cannedInput("/list", msg, privateMsg, "/exit")
    val sa = TestActorRef(new SessionActor)
    sa ! NameValidation(cltName, result = true)
    expectMsg(Register(cltName, sa.getSingleChild(s"clt_$cltName")))
    sa ! Authorized(cltName)
    expectMsg(GetOnlineClients)
    expectMsg(Message(msg, cltName))
    expectMsg(PrivateMessage(msg, cltName, target))
    expectMsg(Unregister(cltName))
  }
}
