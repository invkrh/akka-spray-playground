import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/2/15
 * Time: 9:46 PM
 */

import Event._

class ClientActor(val id: String) extends Actor {
  override def receive: Receive = {
    case msg@Message(txt, sdrid) => print(s"\n\n[$sdrid> $txt]\n\nme > ")
    case ClientList(ids)         => print("\n[" + (ids mkString ", ") + "]\n\nme > ")
  }
}

object ClientApp extends App {

  val id     = readLine()
  val system = ActorSystem("AkkaChat", ConfigFactory.load.getConfig("client-node"))

  val serverPort    = 2552
  val serverAddress = "127.0.0.1"
  val server        = system.actorSelection(s"akka.tcp://AkkaChat@$serverAddress:$serverPort/user/chatserver")

  val client = system.actorOf(Props(classOf[ClientActor], id), name = s"client_$id")
  server.tell(Register(id), client)

  print("me > ")
  Iterator.continually(readLine()).takeWhile(_ != "/exit").foreach {
    case "/list"     => server.tell(GetOnlineClients, client)
    case txt: String =>
      server.tell(Message(txt, id), client)
      print("me > ") // for continuous input prompt
  }

  println("Exiting...")
  server.tell(Unregister, client)

}
