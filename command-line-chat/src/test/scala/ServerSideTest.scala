import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import com.typesafe.config.ConfigFactory
import event._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

import scala.util.{Failure, Success, Random}

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/5/15
 * Time: 12:25 AM
 */

class ServerSideTest extends TestKit(ActorSystem("testSystem"))
with ImplicitSender
with FlatSpecLike
with Matchers
with ScalaFutures
with BeforeAndAfterAll {

  /**
   * common test settings
   */

  val nbClient   = 30
  val serverRef  = TestActorRef[ServerActor]
  val sever      = serverRef.underlyingActor
  val clients    = Array.tabulate(nbClient)(_ => (Random.alphanumeric.take(10).toArray.mkString, TestProbe())).toMap
  // map remove duplicated keys if exists
  val rdClientId = clients.keys.toArray.apply(Random.nextInt(nbClient))
  sever.idToRef = clients.mapValues(_.ref)

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

  //TODO: add register event test case

  it should "list all registered client" in {
    serverRef.tell(GetOnlineClients, clients(rdClientId).ref)
    clients(rdClientId).expectMsg(ClientList(clients.keys.toSet))
  }

  it should "send msg to all registered users except the msg sender" in {
    serverRef.tell(Message("testMsg", rdClientId), clients(rdClientId).ref)
    clients(rdClientId).expectNoMsg()
    (clients - rdClientId).values foreach (_.expectMsg(Message("testMsg", rdClientId)))
  }

  it should "list all registered client except the unregistered ones" in {
    serverRef.tell(Unregister(rdClientId), clients(rdClientId).ref)
    sever.idToRef.contains(rdClientId) shouldBe false
  }

}
