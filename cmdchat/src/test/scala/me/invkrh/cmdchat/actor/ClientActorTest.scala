package me.invkrh.cmdchat.actor

import akka.actor.{ActorSystem, PoisonPill}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import me.invkrh.cmdchat.event._
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/12/15
 * Time: 12:50 PM
 */

class ClientActorTest extends TestKit(ActorSystem("testSystem"))
with ImplicitSender
with FlatSpecLike
with Matchers
with BeforeAndAfterAll {

  val msg        = "this is a msg"
  val clientName = "tester"
  val clientRef  = TestActorRef(new ClientActor(clientName))

  "ClientActor" should "println notification when membership changed" in {
    clientRef ! MemberChanged("A", isExists = true)
    expectNoMsg()
    clientRef ! MemberChanged("A", isExists = false)
    expectNoMsg()
  }

  "ClientActor" should "reply nothing when receiving a msg" in {
    clientRef ! Message("this is a message", "")
    expectNoMsg()
  }

  "ClientActor" should "reply nothing when receiving a client list" in {
    clientRef ! ClientList(Set("A", "B"))
    expectNoMsg()
  }

  "ClientActor" should "reply nothing when receiving a private message" in {
    clientRef ! PrivateMessage("this is a message", "", "")
    expectNoMsg()
  }

  "ClientActor" should "reply nothing when the target of a private message is not available" in {
    clientRef ! TargetNotExist("", "")
    expectNoMsg()
  }

  "ClientActor" should "be killed when PoisonPill is received" in {
    val dyingClient = TestActorRef(new ClientActor(clientName))
    val ctx = dyingClient.underlyingActor.context
    dyingClient ! PoisonPill
    Thread.sleep(5000)
    ctx.system.isTerminated shouldBe true
  }

}
