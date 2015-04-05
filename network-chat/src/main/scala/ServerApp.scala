import Event._
import akka.actor._
import com.typesafe.config.ConfigFactory

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/3/15
 * Time: 10:42 PM
 */

class ServerActor extends Actor {

  var idToRef = Map[String, ActorRef]()

  override def receive: Receive = {
    case msg@Message(txt, id) =>
      idToRef.values filter (_ != sender) foreach (_ forward msg)
    case GetOnlineClients     =>
      sender ! ClientList(idToRef.keys.toSet)
    case Register(id)         =>
      idToRef += id -> sender
      println(s"server> $id has registered")
    case Unregister(id)       =>
      idToRef -= id
      sender ! PoisonPill
  }
}

object ServerApp extends App {
  val system = ActorSystem("AkkaChat", ConfigFactory.load.getConfig("server-node"))
  val server = system.actorOf(Props[ServerActor], name = "chatserver")
}
