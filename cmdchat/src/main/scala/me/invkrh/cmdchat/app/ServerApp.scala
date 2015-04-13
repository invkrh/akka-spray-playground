package me.invkrh.cmdchat.app

import akka.actor._
import com.typesafe.config.ConfigFactory
import me.invkrh.cmdchat.actor.ServerActor

object ServerApp {

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("AkkaChat", ConfigFactory.load.getConfig("server-node"))
      .actorOf(Props[ServerActor], name = "chatserver")
  }

}
