import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import me.invkrh.cmdchat._
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

import scala.collection.JavaConversions._
import java.io.{ByteArrayInputStream, SequenceInputStream}

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/5/15
 * Time: 11:04 PM
 */

//TODO: redo tests
class ClientSideTest extends TestKit(ActorSystem("testSystem"))
with ImplicitSender
with FlatSpecLike
with Matchers
with BeforeAndAfterAll {

  def cannedInput(cmds: String*) = {
    val inputs = cmds.toIterator
      .map(x => new ByteArrayInputStream((x + "\n").getBytes))
    val in = new SequenceInputStream(inputs)
    Console.setIn(in)
  }

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

  "ClientActor" should "println notification when membership changed" in {
    val clientRef = TestActorRef(new ClientActor(""))
    clientRef ! MemberChanged("A", true)
    expectNoMsg()
    clientRef ! MemberChanged("A", false)
    expectNoMsg()
  }

  "ClientActor" should "reply nothing when receiving a msg" in {
    val clientRef = TestActorRef(new ClientActor(""))
    clientRef ! Message("this is a message", "")
    expectNoMsg()
  }

  "ClientActor" should "reply nothing when receiving a client list" in {
    val clientRef = TestActorRef(new ClientActor(""))
    clientRef ! ClientList(Set("A", "B"))
    expectNoMsg()
  }

  "ClientApp" should "work well with correct user input" in {
    cannedInput("A", "/list", "/exit")
    ClientApp.main(Array())
  }

  "ClientApp" should "shut down the system when server is not available" in {
    ClientApp.startSession("1.1.1.1", 4040)
    //wait system to stop
    Thread.sleep(5000)
    ClientApp.system.isTerminated shouldBe true
  }

  "SessionActor" should "register client on server side when name is valid" in {
    cannedInput("A")
    // self is the server
    val sa = TestActorRef(new SessionActor(self))
    expectMsg(NameCheck("A"))
    sa ! NameValidation(true, "A")
    expectMsg(Register(sa.getSingleChild("clt_A"), "A"))
  }

  "SessionActor" should "retry name check when name is not valid" in {
    cannedInput("A", "B")
    // self is the server
    val sa = TestActorRef(new SessionActor(self))
    expectMsg(NameCheck("A"))
    sa ! NameValidation(false, "")
    expectMsg(NameCheck("B"))
  }

  "SessionActor" should "work correctly when client is registered" in {
    cannedInput("A", "/list", "this a msg", "/exit")
    // self is the server
    val sa = TestActorRef(new SessionActor(self))
    expectMsg(NameCheck("A"))
    sa ! NameValidation(true, "A")
    val clt = sa.getSingleChild("clt_A")
    expectMsg(Register(clt, "A"))
    sa ! Authorized(clt, "A")
  }

}
