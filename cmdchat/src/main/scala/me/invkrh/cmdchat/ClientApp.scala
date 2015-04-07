package me.invkrh.cmdchat

import akka.actor._
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import me.invkrh.cmdchat.Printer._

import scala.concurrent.duration._
import scala.util.{Failure, Success}


/**
 * The actor manage the session between server and client.
 * (login, send message, quit, etc)
 *
 * @param server
 * @param client
 */
class SessionActor(server: ActorRef, client: ActorRef) extends Actor {

  def checkName() = {
    val name = readLine("\nPlease enter you name: ")

    /**
     * SessionActor sends this msg to server,
     * while server will register client
     */
    server ! Register(client, name)
  }

  override def preStart() {
    checkName()
  }

  override def receive: Actor.Receive = {
    case NameVerification(true, name) =>
      import NameHolder._
      clientName = name // save name for later prompt use
      notification("Server", s"Hey, $name. You are connected!", prompt())
      Iterator.continually(readLine()).takeWhile(_ != "/exit").foreach {
        case "/list"     => server.tell(GetOnlineClients, client)
        case txt: String =>
          server.tell(Message(txt, name), client)
          print(s"\nme ($name) > ") // for continuous input prompt
      }
      println("Exiting...")
      server.tell(Unregister(name), client)
      context.system.shutdown()
    case NameVerification(false, _)   =>
      notification("Server", "Name occupied, please try another ...")
      checkName()
  }
}

class ClientActor extends Actor {

  import NameHolder._

  override def receive: Receive = {
    case ClientList(ids)       => cmdResultDisplay(ids mkString ", ", prompt())
    case NewComer(another)     => notification("Server", s"$another has joined in the group", prompt())
    case SomeOneLeave(another) => notification("Server", s"$another has left the group", prompt())
    case msg@Message(txt, sdr) => incomingMSGDisplay(sdr, txt, prompt())
  }
}

object ClientApp {

  def main(args: Array[String]) = {

    val system = ActorSystem("AkkaChat", ConfigFactory.load.getConfig("client-node"))
    val serverPort = 2552
    val serverAddress = "127.0.0.1"

    // validation
    import scala.concurrent.ExecutionContext.Implicits.global
    implicit val timeout = Timeout(5 seconds)
    system.actorSelection(s"akka.tcp://AkkaChat@$serverAddress:$serverPort/user/chatserver")
      .resolveOne()
      .onComplete {
      case Success(server) => // Server is ready
        val client = system.actorOf(Props(classOf[ClientActor]))
        system.actorOf(Props(classOf[SessionActor], server, client))
      case Failure(_)      =>
        notification("System", "service not available!")
        system.shutdown()
    }

  }

}
