import akka.actor.Actor
import akka.event.Logging

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 3/29/15
 * Time: 9:59 PM
 */

class MyActor(number: Int) extends Actor {
  val log = Logging(context.system, this)
  override def receive: Receive = {
    case "test" => println("test value = " + number)
    case _      => println("received unknown message")
  }
}
