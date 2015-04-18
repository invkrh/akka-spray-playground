package me.invkrh.cmdchat.app

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import me.invkrh.cmdchat.mock.MockUserInput
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/12/15
 * Time: 12:58 PM
 */

class ServerAppTest extends TestKit(ActorSystem("testSystem"))
with ImplicitSender
with MockUserInput
with FlatSpecLike
with Matchers
with BeforeAndAfterAll {

  "ServerApp" should "work well with correct user input" in {
    ServerApp.main(Array())
  }

}
