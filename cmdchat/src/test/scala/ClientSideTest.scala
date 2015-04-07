import java.io.{SequenceInputStream, ByteArrayInputStream}

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import me.invkrh.cmdchat.{ClientActor, ClientList, Message}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/5/15
 * Time: 11:04 PM
 */

class ClientSideTest extends TestKit(ActorSystem("testSystem"))
with ImplicitSender
with FlatSpecLike
with Matchers
with BeforeAndAfterAll {

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

  it should "reply nothing when receiving a msg" in {
    val clientRef = TestActorRef(new ClientActor())
    clientRef ! Message("this is a message", "testActor")
    expectNoMsg()
  }

  it should "reply nothing when receiving a client list" in {
    val clientRef = TestActorRef(new ClientActor())
    clientRef ! ClientList(Set("A", "B"))
    expectNoMsg()
  }

  //TODO: auto SequenceInputStream creation, add tests for all code
  "readLine" should "work" in {
    val name = new ByteArrayInputStream("A".getBytes)
    val list = new ByteArrayInputStream("/list".getBytes)
    val exit = new ByteArrayInputStream("/exit".getBytes)
    val inputs = new SequenceInputStream(name, new SequenceInputStream(list , exit))
    Console.setIn(inputs)
    readLine() === "A"
    readLine() === "/list"
    readLine() === "/exit"
  }
}
