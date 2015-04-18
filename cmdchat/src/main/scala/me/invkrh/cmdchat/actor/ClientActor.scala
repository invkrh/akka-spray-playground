package me.invkrh.cmdchat.actor

import akka.actor.Actor
import me.invkrh.cmdchat.event._
import me.invkrh.cmdchat.util.Display

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/12/15
 * Time: 12:32 PM
 */
class ClientActor(val name: String) extends Actor with Display{

  val prompt = getPrompt(name)

  // once Client actor is killed, system should be down
  override def postStop(): Unit = context.system.shutdown()

  override def receive: Receive = {
    case ClientList(ids)                         => response(ids mkString ", ", prompt)
    case MemberChanged(another, res)             => incomingMsg("Server", s"$another has ${if (res) "joined in" else "left"} the group", prompt)
    case msg@Message(txt, msgSdr)                => incomingMsg(msgSdr, txt, prompt)
    case msg@PrivateMessage(txt, msgSdr, target) => incomingMsg(msgSdr + " (in private)", txt, prompt)
    case TargetNotExist(txt, target)             => incomingMsg("Server", s"$target is not registered yet ! Msg <$txt> is not delivered", prompt)
  }

}
