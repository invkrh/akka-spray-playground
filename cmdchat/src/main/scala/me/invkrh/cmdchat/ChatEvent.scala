package me.invkrh.cmdchat

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/5/15
 * Time: 12:59 AM
 */

sealed trait ChatEvent

case object GetOnlineClients extends ChatEvent

case class NameCheck(name: String) extends ChatEvent

case class NameValidation(name: String, result: Boolean) extends ChatEvent

case class Register(name: String) extends ChatEvent

case object Authorized extends ChatEvent

case class Unregister(name: String) extends ChatEvent

case class MemberChanged(name: String, isExists: Boolean) extends ChatEvent

case class Message(text: String, senderId: String) extends ChatEvent

case class ClientList(clients: Set[String]) extends ChatEvent

