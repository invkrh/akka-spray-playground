import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import event.{ClientList, Message}
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
    val clientRef = TestActorRef(new ClientActor("clt"))
    clientRef ! Message("this is a message", "testActor")
    expectNoMsg()
  }

  it should "reply nothing when receiving a client list" in {
    val clientRef = TestActorRef(new ClientActor("clt"))
    clientRef ! ClientList(Set("A", "B"))
    expectNoMsg()
  }
}
