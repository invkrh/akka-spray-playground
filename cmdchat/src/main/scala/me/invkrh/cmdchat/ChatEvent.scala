package me.invkrh.cmdchat

import akka.actor.ActorRef

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/5/15
 * Time: 12:59 AM
 */

//TODO: refactor actor to remove ActorRef everywhere

sealed trait ChatEvent

case class GetOnlineClients(client: ActorRef) extends ChatEvent

case class NameCheck(name: String) extends ChatEvent

case class NameValidation(result: Boolean, name: String) extends ChatEvent

case class Register(client: ActorRef, name: String) extends ChatEvent

case class Authorized(client: ActorRef, name: String) extends ChatEvent

case class Unregister(client: ActorRef, id: String) extends ChatEvent

case class MemberChanged(name: String, isExists: Boolean) extends ChatEvent

case class Message(text: String, senderId: String) extends ChatEvent

case class ClientList(clients: Set[String]) extends ChatEvent

