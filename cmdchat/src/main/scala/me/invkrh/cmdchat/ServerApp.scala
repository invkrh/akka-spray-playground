package me.invkrh.cmdchat

import akka.actor._
import com.typesafe.config.ConfigFactory

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/3/15
 * Time: 10:42 PM
 */

class ServerActor extends Actor {

  /**
   * Some => registered client
   * None => client having reserved a name
   */
  var idToRef = Map[String, Option[ActorRef]]()

  override def receive: Receive = {
    case NameCheck(id@name)     => //sender is SessionActor
      if (idToRef.contains(name)) {
        sender() ! NameValidation(result = false, name)
      } else {
        sender() ! NameValidation(result = true, name)
        idToRef += name -> None
      }
    case msg@Message(txt, name) =>
      idToRef.filter(p => p._1 != name).values.foreach {
        case Some(member) => member forward msg
        case None         =>
      }
    case GetOnlineClients(requester)       =>
      requester ! ClientList(idToRef.filter(_._2 != None).keys.toSet)
    case Register(client, name) =>
      idToRef.values foreach {
        case Some(member) => member ! MemberChanged(name, isExists = true)
        case None         =>
      }
      idToRef += name -> Some(client)
      sender ! Authorized(client, name)
      println(s"server > $name has joined")
    case Unregister(client, name)       =>
      idToRef -= name
      idToRef.values foreach {
        case Some(member) => member ! MemberChanged(name, isExists = false)
        case None         =>
      }
      client ! PoisonPill
      println(s"server > $name has left")
  }
}

object ServerApp extends App {
  ActorSystem("AkkaChat", ConfigFactory.load.getConfig("server-node"))
    .actorOf(Props[ServerActor], name = "chatserver")
}
