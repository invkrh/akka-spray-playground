package me.invkrh.cmdchat

import akka.actor.{ActorRef, Actor, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/2/15
 * Time: 9:46 PM
 */

import me.invkrh.cmdchat.event._

object helper {
  def cmdResultDisplay(txt: String, appendPrompt: Boolean = true) = {
    print(s"\n[$txt]\n\n" + (if (appendPrompt) "me > " else ""))
  }

  def notification(sdr: String, txt: String, appendPrompt: Boolean = true) = {
    print(s"\n\n[$sdr > $txt]\n\n" + (if (appendPrompt) "me > " else ""))
  }

  def incomingMSGDisplay(sdr: String, txt: String, appendPrompt: Boolean = true) = {
    print(s"\n|\n|\t\t\t\t\t\t\t\t\t\t[$sdr > $txt]\n|\n" + (if (appendPrompt) "me > " else ""))
  }
}

import me.invkrh.cmdchat.helper._

class ClientActor(val name: String) extends Actor {
  override def receive: Receive = {
    case ClientList(ids)       => cmdResultDisplay(ids mkString ", ")
    case NewComer(another)     => notification("Server", s"$another has joined in the group")
    case SomeOneLeave(another) => notification("Server", s"$another has left the group")
    case msg@Message(txt, sdr) => incomingMSGDisplay(sdr, txt)
  }
}

object ClientApp {

  def checkName(server: ActorRef): String = {
    print("\n\nPlease enter you name: ")
    val name = readLine()
    implicit val timeout = Timeout(5 seconds)
    //TODO: can not send client ref => can not register sender
    val future = (server ? Register(name)).mapTo[Boolean]
    if (Await.result(future, timeout.duration)) {
      name
    } else {
      notification("Server", "Name occupied, please try another ...", appendPrompt = false)
      checkName(server)
    }
  }

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
      case Success(serverRef) => // Server is ready

        // validate name by blocking
        val name = checkName(serverRef)
        val client = system.actorOf(Props(classOf[ClientActor], name))

        notification("Server", s"Hey, $name. You are connected!")
        Iterator.continually(readLine()).takeWhile(_ != "/exit").foreach {
          case "/list"     => serverRef.tell(GetOnlineClients, client)
          case txt: String =>
            serverRef.tell(Message(txt, name), client)
            print("\nme > ") // for continuous input prompt
        }
        println("Exiting...")
        serverRef.tell(Unregister(name), client)
        system.shutdown()

      case Failure(_) =>
        notification("System", "service not available!", appendPrompt = false)
        system.shutdown()
    }

  }

}
