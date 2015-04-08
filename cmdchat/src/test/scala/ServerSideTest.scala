import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import me.invkrh.cmdchat._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

import scala.util.Random

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

  def settings(nbClient: Int = 30) = {
    val serverRef = TestActorRef[ServerActor]
    val server = serverRef.underlyingActor
    val clients = Array.tabulate(nbClient)(_ => (Random.alphanumeric.take(10).toArray.mkString, TestProbe())).toMap
    // map remove duplicated keys if exists
    val rdClientId = clients.keys.toArray.apply(Random.nextInt(nbClient))
    server.idToRef = clients.mapValues(v => Some(v.ref))
    (serverRef, clients, rdClientId)
  }

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

  it should "register client correctly" in {
    val (serverRef, _, _) = settings()
    val newClient = TestProbe()
    serverRef.tell(Register(newClient.ref, "123"), newClient.ref)
    serverRef.underlyingActor.idToRef.contains("123") shouldBe true
  }

  it should "list all registered client" in {
    val (serverRef, clients, rdClientId) = settings()
    serverRef.tell(GetOnlineClients, clients(rdClientId).ref)
    clients(rdClientId).expectMsg(ClientList(clients.keys.toSet))
  }

  it should "send msg to all registered users except the msg sender" in {
    val (serverRef, clients, rdClientId) = settings()
    serverRef.tell(Message("testMsg", rdClientId), clients(rdClientId).ref)
    clients(rdClientId).expectNoMsg()
    (clients - rdClientId).values foreach (_.expectMsg(Message("testMsg", rdClientId)))
  }

  it should "list all registered client except the unregistered ones" in {
    val (serverRef, clients, rdClientId) = settings()
    serverRef.tell(Unregister(rdClientId), clients(rdClientId).ref)
    serverRef.underlyingActor.idToRef.contains(rdClientId) shouldBe false
  }

  "ServerActor" should "check name" in {
    val (serverRef, _, rdClientId) = settings()
    serverRef ! NameCheck(rdClientId)
    expectMsg(NameValidation(false, rdClientId))
    serverRef ! NameCheck("123")
    expectMsg(NameValidation(true, "123"))
  }

  //TODO: complete and refactor test
  "ServerActor" should "do broadcast correctly when some names are reserved" in {
    val (serverRef, _, innerProbeName) = settings()
    val innerRef = serverRef.underlyingActor.idToRef(innerProbeName).get
    // reserver a name
    val nm = "123"
    serverRef ! NameCheck(nm)
    expectMsg(NameValidation(true, nm))
    // broadcast unregister event
    serverRef ! Unregister(innerProbeName)
    expectNoMsg()
    // broadcast register event
    serverRef ! NameCheck(innerProbeName)
    expectMsg(NameValidation(true, innerProbeName))
    val pb1 = TestProbe().ref
    serverRef ! Register(pb1, innerProbeName)
    expectMsg(Authorized(pb1, innerProbeName))
    // broadcast client list
    serverRef ! GetOnlineClients
    expectMsg(ClientList(serverRef.underlyingActor.idToRef.keys.toSet))
    val pb2 = TestProbe().ref
    serverRef ! Register(pb2, nm)
    expectMsg(Authorized(pb2, nm))
  }

  "ServerApp" should "start correctly" in {
    // this test is considered O.K. if no exception is thrown
    ServerApp.main(Array())
  }

}
