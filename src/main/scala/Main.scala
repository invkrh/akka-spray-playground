import akka.actor.{ActorSystem, Props}

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 3/29/15
 * Time: 7:02 PM
 */

object Main extends App{

  val system = ActorSystem("mySystem")
  val myActor = system.actorOf(Props(new MyActor(2)), name = "myactor")

  myActor ! "test"

  system.shutdown()
}
