package actor

import akka.actor.{Terminated, PoisonPill, ActorSystem}
import akka.testkit.{TestProbe, TestActorRef, ImplicitSender, TestKit}
import me.invkrh.cmdchat.actor.ServerActor
import me.invkrh.cmdchat.event._
import mock.MockUserInput
import org.scalatest.{BeforeAndAfterAll, Matchers, FlatSpecLike}

import scala.util.Random

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/12/15
 * Time: 12:56 PM
 */

class ServerActorClass extends TestKit(ActorSystem("testSystem"))
with ImplicitSender
with MockUserInput
with FlatSpecLike
with Matchers
with BeforeAndAfterAll {

  val testMsg = "this is a test message"

  /**
   * common test settings
   */

  def settings(nbClient: Int = 5) = {
    val serverRef = TestActorRef[ServerActor]
    // transform to map in order to  remove duplicated keys if exists
    val clients = Array.tabulate(nbClient)(_ => (Random.alphanumeric.take(10).toArray.mkString, TestProbe())).toMap
    // register client
    clients.values.map(_.ref).foreach(serverRef.watch)
    val rdClientId = clients.keys.toArray.apply(Random.nextInt(nbClient))
    serverRef.underlyingActor.idToRef = clients.mapValues(v => Some(v.ref))
    (serverRef, clients, rdClientId)
  }

  "ServerActor" should "register client correctly" in {
    val (serverRef, _, _) = settings()
    serverRef ! Register("123", TestProbe().ref)
    serverRef.underlyingActor.idToRef.contains("123") shouldBe true
    expectMsg(Authorized("123"))
  }

  "ServerActor" should "list all registered client" in {
    val (serverRef, clients, _) = settings()
    serverRef ! GetOnlineClients
    expectMsg(ClientList(clients.keys.toSet))
  }

  "ServerActor" should "send msg to all registered users except the msg sender" in {
    val (serverRef, clients, rdClientId) = settings()
    val clt = clients(rdClientId)
    serverRef.tell(Message(testMsg, rdClientId), clt.ref)
    clt.expectNoMsg()
    (clients - rdClientId).values foreach (_.expectMsg(Message(testMsg, rdClientId)))
  }

  "ServerActor" should "send private msg only to the target user" in {
    val (serverRef, clients, rdClientId) = settings()
    serverRef ! PrivateMessage(testMsg, "anyOne", rdClientId)
    clients(rdClientId).expectMsg(PrivateMessage(testMsg, "anyOne", rdClientId))
    (clients - rdClientId).values foreach (_.expectNoMsg())
    serverRef ! PrivateMessage(testMsg, "anyOne", "notExist")
    expectMsg(TargetNotExist(testMsg, "notExist"))
  }

  "ServerActor" should "broadcast membership change to all registered client" in {
    val (serverRef, clients, rdClientId) = settings()
    // user probe, prevent implicit sender being killed
    val pickedClt = clients(rdClientId)
    val listener = TestProbe()
    listener watch pickedClt.ref
    serverRef ! Unregister(rdClientId)
    listener.expectTerminated(pickedClt.ref)
    serverRef.underlyingActor.idToRef.contains(rdClientId) shouldBe false
    (clients - rdClientId).values foreach (_.expectMsg(MemberChanged(rdClientId, isExists = false)))
  }

  "ServerActor" should "check name" in {
    val (serverRef, _, rdClientId) = settings()
    serverRef ! NameCheck(rdClientId)
    expectMsg(NameValidation(rdClientId, result = false))
    serverRef ! NameCheck("123")
    expectMsg(NameValidation("123", result = true))
  }

}
