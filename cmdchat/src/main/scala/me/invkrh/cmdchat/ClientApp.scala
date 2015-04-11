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
 */

class SessionActor(server: ActorRef) extends Actor {

  def checkName() = {
    val name = readLine("\nPlease enter you name: ")
    server ! NameCheck(name)
  }

  override def preStart() {
    checkName()
  }

  override def receive: Receive = {
    case NameValidation(true, name)  =>
      val client = context.actorOf(Props(classOf[ClientActor], name), s"clt_$name")
      server.tell(Register(name), client)
    case NameValidation(false, name) =>
      showNotification("Server", s"Name [ $name ] is occupied, please try another ...")
      checkName()
  }
}

class ClientActor(val name: String) extends Actor {
  val prompt = getPrompt(name)

  // once Client actor is killed, system should be down
  override def postStop(): Unit = {
    context.system.shutdown()
  }

  override def receive: Receive = {
    case ClientList(ids)               => showInputRes(ids mkString ", ", prompt)
    case MemberChanged(another, true)  => showNotification("Server", s"$another has joined in the group", prompt)
    case MemberChanged(another, false) => showNotification("Server", s"$another has left the group", prompt)
    case msg@Message(txt, msgSdr)      => showIncomingMsg(msgSdr, txt, prompt)
    case Authorized                    => // sender is server
      showNotification("Server", s"Hey, $name. You are connected!", getPrompt(name))
      Iterator.continually(readLine()).takeWhile(_ != "/exit").foreach {
        case "/list"     => sender() ! GetOnlineClients
        case txt: String =>
          sender() ! Message(txt, name)
          print(s"\nme ($name) > ") // for continuous input prompt
      }
      println("Exiting...")
      sender() ! Unregister(name)
  }
}

object ClientApp {
  val system = ActorSystem("AkkaChat", ConfigFactory.load.getConfig("client-node"))

  def startSession(serverAddr: String, port: Int) = {
    // validation
    import scala.concurrent.ExecutionContext.Implicits.global
    // need to resolve server in 5 seconds
    implicit val timeout = Timeout(3 seconds)
    system.actorSelection(s"akka.tcp://AkkaChat@$serverAddr:$port/user/chatserver")
      .resolveOne()
      .onComplete {
      case Success(server) => // Server is ready
        system.actorOf(Props(classOf[SessionActor], server))
      case Failure(_)      =>
        showNotification("System", "service not available!")
        system.shutdown()
    }
  }

  def main(args: Array[String]) = {
    startSession("127.0.0.1", 2552)
  }

}
