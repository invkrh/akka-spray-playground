package me.invkrh.cmdchat.event

import akka.actor.ActorRef

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/13/15
 * Time: 10:45 PM
 */

sealed trait Event

case class Authorized(name: String) extends Event

case class NameCheck(name: String) extends Event

case class NameValidation(name: String, result: Boolean) extends Event

case class Register(name: String, client: ActorRef) extends Event

case class Unregister(name: String) extends Event

case class MemberChanged(name: String, isExists: Boolean) extends Event

case class Message(text: String, senderId: String) extends Event

case object GetOnlineClients extends Event

case class ClientList(clients: Set[String]) extends Event