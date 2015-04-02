import Entity.ChatClientActor
import akka.actor.{Props, ActorSystem}

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/2/15
 * Time: 9:46 PM
 */

import Entity._

object networkChatMain extends App {
  val id            = readLine()
  val system        = ActorSystem("AkkaChat")
  val serverAddress = "localhost"
  val serverPort    = "80"

  val server = system.actorSelection(s"akka.tcp://AkkaChat@$serverAddress:$serverPort/user/chatserver")

  val client = system.actorOf(Props(classOf[ChatClientActor], server, id), name = id)

  Iterator.continually(readLine()).takeWhile(_ != "/exit").foreach {
    case "/list" =>
      server.tell(ShowClientList, client)

    case "/join" =>
      server.tell(Register, client)

    case msg =>
      server.tell(Chat(msg), client)
  }

  println("Exiting...")
  server.tell(Unregister, client)

}
