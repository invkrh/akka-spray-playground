package me.invkrh.cmdchat.actor

import akka.actor.{Actor, ActorRef, PoisonPill, Terminated}
import me.invkrh.cmdchat.event._

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/3/15
 * Time: 10:42 PM
 */

class ServerActor extends Actor {

  override def postStop(): Unit = {
    context.system.shutdown()
  }

  /**
   * Some => registered client
   * None => client having reserved a name
   */
  var idToRef = Map[String, Option[ActorRef]]()

  override def receive: Receive = {

    /**
     * sender is SessionActor
     */

    case NameCheck(id@name) => //sender is SessionActor
      if (idToRef.contains(name)) {
        sender() ! NameValidation(name, result = false)
      } else {
        sender() ! NameValidation(name, result = true)
        idToRef += name -> None
      }

    case Register(name, client) =>
      println(s"server > $name has joined")
      idToRef.values.foreach {
        case Some(member) => member ! MemberChanged(name, isExists = true)
        case None         =>
      }
      idToRef += name -> Some(client)
      context.watch(client)
      sender() ! Authorized(name)

    case Unregister(name) =>
      idToRef(name).get ! PoisonPill

    /**
     * sender is ClientActor
     */

    case Terminated(dead) =>
      val name = idToRef.find(_._2 == Some(dead)) match {
        case Some((deadName, _)) => deadName
        case None                => throw new NoSuchElementException
      }
      println(s"server > $name has left")
      idToRef -= name
      idToRef.values.foreach {
        case Some(member) => member ! MemberChanged(name, isExists = false)
        case None         =>
      }

    case msg@PrivateMessage(txt, sdrName, target) =>
      idToRef.getOrElse(target, None) match {
        case Some(member) => member forward msg
        case None         => sender() ! TargetNotExist(txt, target)
      }

    case msg@Message(txt, sdrName) =>
      idToRef.filter(_._1 != sdrName).values.foreach {
        case Some(member) => member forward msg
        case None         =>
      }

    case GetOnlineClients =>
      sender() ! ClientList(idToRef.filter(_._2 != None).keys.toSet)
  }

}
