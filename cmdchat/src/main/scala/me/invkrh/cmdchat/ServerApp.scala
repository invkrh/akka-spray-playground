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

  var idToRef = Map[String, ActorRef]()

  override def receive: Receive = {
    case msg@Message(txt, name) =>
      idToRef.values filter (_ != sender) foreach (_ forward msg)
    case GetOnlineClients       =>
      sender ! ClientList(idToRef.keys.toSet)
    case Register(client, name)         =>
      if (idToRef.contains(name)) {
        sender() ! NameVerification(result = false, "")
      } else {
        idToRef.values foreach (_ ! NewComer(name))
        sender() ! NameVerification(result = true, name)
        idToRef += name -> client
        println(s"server > $name has registered")
      }
    case Unregister(name)       =>
      idToRef -= name
      idToRef.values foreach (_ ! SomeOneLeave(name))
      sender ! PoisonPill
      println(s"server > $name has left")
  }
}

object ServerApp extends App {
  ActorSystem("AkkaChat", ConfigFactory.load.getConfig("server-node"))
    .actorOf(Props[ServerActor], name = "chatserver")
}
