package Entity

import akka.actor.{Actor, ActorSelection}

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/2/15
 * Time: 11:56 PM
 */

class ChatClientActor(server: ActorSelection, id: String) extends Actor {
  override def receive: Receive = {
    case msg @ Chat(m) => println(s"$sender(): $m")
    case ClientList(set) => set foreach println
  }
}
