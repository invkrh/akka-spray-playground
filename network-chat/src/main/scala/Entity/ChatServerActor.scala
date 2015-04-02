package Entity

import akka.actor.{Actor, ActorRef, PoisonPill}

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/2/15
 * Time: 10:50 PM
 */

class ChatServerActor extends Actor {
  var onlineClients = Set[ActorRef]()

  override def receive: Receive = {
    case msg@Chat(m)    => onlineClients filter (_ != sender) foreach (_ forward msg)
    case ShowClientList => sender ! onlineClients
    case Register       => onlineClients += sender
    case Unregister     =>
      onlineClients -= sender
      sender ! PoisonPill
  }
}
