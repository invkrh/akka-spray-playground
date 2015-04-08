import java.io.{SequenceInputStream, ByteArrayInputStream}

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import me.invkrh.cmdchat.{ClientApp, ClientActor, ClientList, Message}
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
    val clientRef = TestActorRef(new ClientActor(""))
    clientRef ! Message("this is a message", "")
    expectNoMsg()
  }

  it should "reply nothing when receiving a client list" in {
    val clientRef = TestActorRef(new ClientActor(""))
    clientRef ! ClientList(Set("A", "B"))
    expectNoMsg()
  }

  "Client App" should "work" in {
    import scala.collection.JavaConversions.asJavaEnumeration
    import java.io.{ByteArrayInputStream, SequenceInputStream}

    val inputs = Iterator("A", "/list", "/exit")
      .map(x => new ByteArrayInputStream((x + "\n").getBytes))
    val in = new SequenceInputStream(asJavaEnumeration(inputs))
    Console.setIn(in)

    ClientApp.main(Array())
  }
}
