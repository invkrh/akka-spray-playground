package me.invkrh.cmdchat

import akka.actor.ActorRef

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/5/15
 * Time: 12:59 AM
 */

sealed trait ChatEvent

case object GetOnlineClients extends ChatEvent

case class Register(client: ActorRef, name: String) extends ChatEvent

case class NameVerification(result: Boolean, name: String) extends ChatEvent

case class Unregister(id: String) extends ChatEvent

// TODO: reduce new/leave to one event
case class NewComer(id: String) extends ChatEvent

case class SomeOneLeave(id: String) extends ChatEvent

case class Message(text: String, senderId: String) extends ChatEvent

case class ClientList(clients: Set[String]) extends ChatEvent

