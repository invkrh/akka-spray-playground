import Event._
import akka.actor.ActorSystem
import akka.testkit.{TestProbe, TestActorRef, ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/5/15
 * Time: 12:25 AM
 */

class ServerActorTest extends TestKit(ActorSystem("testSystem")) with ImplicitSender with FlatSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

  "Server Actor" should "list all registered client" in {
    val clt1 = "Alice"
    val clt2 = "Bob"
    val serverRef = TestActorRef[ServerActor]
    val sever = serverRef.underlyingActor

    sever.idToRef = Map(
      clt1 -> TestProbe().ref,
      clt2 -> TestProbe().ref
    )
    serverRef ! GetOnlineClients
    expectMsg(ClientList(Set[String](clt1, clt2)))
  }

  it should "send msg to all registered users except the msg sender" in {

    val clt1 = TestProbe()
    val clt2 = TestProbe()
    val serverRef = TestActorRef[ServerActor]
    val sever = serverRef.underlyingActor

    sever.idToRef = Map(
      "Alice" -> clt1.ref,
      "Bob" -> clt2.ref
    )
    serverRef.tell(Message("testMsg", "Alice"), clt1.ref)
    clt2.expectMsg(Message("testMsg", "Alice"))
    clt1.expectNoMsg()
  }
}
