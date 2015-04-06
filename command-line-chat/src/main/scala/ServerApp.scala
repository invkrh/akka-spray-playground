import akka.actor._
import com.typesafe.config.ConfigFactory
import event._

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

    //TODO: broadcast membership changes
    case Register(id)         =>
      idToRef += id -> sender
      println(s"server > $id has registered")
    case Unregister(id)       =>
      idToRef -= id
      println(s"server > $id has left")
      sender ! PoisonPill
  }
}

object ServerApp extends App {

  ActorSystem("AkkaChat", ConfigFactory.load.getConfig("server-node"))
    .actorOf(Props[ServerActor], name = "chatserver")

}
