package event

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/5/15
 * Time: 12:59 AM
 */

sealed trait ChatEvent

case object GetOnlineClients extends ChatEvent

case class Register(id: String) extends ChatEvent

case class Unregister(id: String) extends ChatEvent

case class Message(text: String, senderId: String) extends ChatEvent

case class ClientList(clients: Set[String]) extends ChatEvent

