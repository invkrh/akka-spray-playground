package me.invkrh.cmdchat.actor

import akka.actor.{Actor, ActorRef, Props}
import me.invkrh.cmdchat.event._
import me.invkrh.cmdchat.util.Printer._

import scala.util.matching.Regex

/**
 * The SessionActor manages the session between the server and a ClientActor created by itself
 * It checks the availability of the given name.
 * If ok, then ask to register the name on sever side
 * If not, then loop name check.
 */

class SessionActor extends Actor {

  // Useful regex to match the private message command
  val privateMessageRegex = """^@([^\s]+) (.*)$""".r

  def working(server: ActorRef, client: ActorRef): Receive = {
    case Authorized(name) =>
      notification("Server", s"Hey, $name. You are connected!", getPrompt(name))
      Iterator.continually(readLine()).takeWhile(_ != "/exit").foreach {
        case "/list"                              => server.tell(GetOnlineClients, client)
        case privateMessageRegex(target, message) =>
          server.tell(PrivateMessage(message, name, target), client)
          print(s"\nme ($name) > ")
        case txt: String                          =>
          server tell(Message(txt, name), client)
          print(s"\nme ($name) > ") // for continuous input prompt
      }
      println("Exiting...")
      server ! Unregister(name)

  }

  override def receive: Receive = {
    case NameValidation(name, res) =>
      if (res) {
        val client = context.actorOf(Props(classOf[ClientActor], name), s"clt_$name")
        sender() ! Register(name, client)
        context.become(working(sender(), client))
      } else {
        notification("Server", s"Name ($name) is occupied, please try another ...")
        sender() ! NameCheck(readLine("\nPlease enter you name: "))
      }
  }

}
