package me.invkrh.cmdchat

import akka.actor._
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import me.invkrh.cmdchat.Printer._

import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
 * The SessionActor manages the session between the server and a ClientActor created by itself
 * It checks the availability of the given name.
 * If ok, then ask to register the name on sever side
 * If not, then loop name check.
 *
 * @param server
 */

class SessionActor(server: ActorRef) extends Actor {

  def checkName() = server ! NameCheck(readLine("\nPlease enter you name: "))

  override def preStart(): Unit = checkName()

  override def receive: Receive = {
    case NameValidation(name, res) =>
      if (res) {
        val client = context.actorOf(Props(classOf[ClientActor], name), s"clt_$name")
        sender().tell(Register(name), client)
      } else {
        notification("Server", s"Name [ $name ] is occupied, please try another ...")
        checkName()
      }
  }
}

class ClientActor(val name: String) extends Actor {
  val prompt = getPrompt(name)

  def working(server: ActorRef) = {
    notification("Server", s"Hey, $name. You are connected!", getPrompt(name))
    Iterator.continually(readLine()).takeWhile(_ != "/exit").foreach {
      case "/list"     => server ! GetOnlineClients
      case txt: String =>
        server ! Message(txt, name)
        print(s"\nme ($name) > ") // for continuous input prompt
    }
    println("Exiting...")
    server ! Unregister(name)
  }

  // once Client actor is killed, system should be down
  override def postStop(): Unit = context.system.shutdown()

  override def receive: Receive = {
    case ClientList(ids)               => response(ids mkString ", ", prompt)
    case MemberChanged(another, true)  => notification("Server", s"$another has joined in the group", prompt)
    case MemberChanged(another, false) => notification("Server", s"$another has left the group", prompt)
    case msg@Message(txt, msgSdr)      => incomingMsg(msgSdr, txt, prompt)
    case Authorized                    => working(sender()) // the client is registered on server side
  }
}

object ClientApp {
  val system = ActorSystem("AkkaChat", ConfigFactory.load.getConfig("client-node"))

  def startSession(serverAddr: String, port: Int) = {
    import scala.concurrent.ExecutionContext.Implicits.global
    implicit val timeout = Timeout(3 seconds) // need to resolve server in 3 seconds
    system.actorSelection(s"akka.tcp://AkkaChat@$serverAddr:$port/user/chatserver")
      .resolveOne()
      .onComplete {
      case Success(server) => // Server is ready
        system.actorOf(Props(classOf[SessionActor], server))
      case Failure(_)      =>
        notification("System", "service not available!")
        system.shutdown()
    }
  }

  def main(args: Array[String]) = {
    startSession("127.0.0.1", 2552)
  }

}
