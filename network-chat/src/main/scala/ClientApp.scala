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
    case msg@Message(txt, sdrid) => println(s"\n[$sdrid> $txt]")
    case ClientList(ids)         => println(s"\nOnline: \n" + (ids mkString "\n"))
  }
}

object ClientApp extends App {

  val id     = readLine()
  val system = ActorSystem("AkkaChat", ConfigFactory.load.getConfig("client-node"))

  val serverPort    = 2552
  val serverAddress = "127.0.0.1"
  val server        = system.actorSelection(s"akka.tcp://AkkaChat@$serverAddress:$serverPort/user/chatserver")

  val client = system.actorOf(Props(classOf[ClientActor], server, id), name = s"client_$id")
  server.tell(Register(id), client)

  Iterator.continually(readLine()).takeWhile(_ != "/exit").foreach { msg =>
    print("me> ")
    msg match {
      case "/list"     => server.tell(GetOnlineClients, client)
      case txt: String => server.tell(Message(txt, id), client)
    }
  }

  println("Exiting...")
  server.tell(Unregister, client)

}
