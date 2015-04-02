import akka.actor.ActorRef

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/2/15
 * Time: 11:55 PM
 */

package object Entity {

  trait Event

  case object Register extends Event

  case object Unregister extends Event

  case object ShowClientList extends Event

  case class Chat(m: String) extends Event

  case class ClientList(clients: Set[ActorRef]) extends Event

}
