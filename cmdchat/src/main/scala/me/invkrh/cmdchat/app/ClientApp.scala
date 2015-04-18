package me.invkrh.cmdchat.app

import akka.actor._
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import me.invkrh.cmdchat.actor.SessionActor
import me.invkrh.cmdchat.event.NameCheck

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

object ClientApp {

  val system = ActorSystem("AkkaChat", ConfigFactory.load.getConfig("client-node"))

  def startSession(serverAddr: String, port: Int) = {
    import scala.concurrent.ExecutionContext.Implicits.global
    implicit val timeout = Timeout(3 seconds) // need to resolve server in 3 seconds
    system.actorSelection(s"akka.tcp://AkkaChat@$serverAddr:$port/user/chatserver")
      .resolveOne()
      .onComplete {
      case Success(server) => // Server is ready
        val session = system.actorOf(Props(classOf[SessionActor]))
        server.tell(NameCheck(readLine("\nPlease enter you name: ")), session)
      case Failure(_)      =>
        println("Server is not available! Please try later...")
        system.shutdown()
    }
  }

  def main(args: Array[String]) = {
    startSession("127.0.0.1", 2552)
  }

}
