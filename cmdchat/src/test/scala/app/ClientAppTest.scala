package app

import akka.actor.{PoisonPill, Props, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import me.invkrh.cmdchat.actor.ServerActor
import me.invkrh.cmdchat.app.ClientApp
import mock.MockUserInput
import org.scalatest.{BeforeAndAfterAll, Matchers, FlatSpecLike}

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/12/15
 * Time: 12:58 PM
 */

class ClientAppTest extends TestKit(ActorSystem("testSystem"))
with ImplicitSender
with MockUserInput
with FlatSpecLike
with Matchers
with BeforeAndAfterAll {

  "ClientApp" should "work well with correct user input" in {
    ClientApp.main(Array())
  }

  "ClientApp" should "shut down the system when server is not available" in {
    ClientApp.startSession("1.1.1.1", 4040)
    //wait system to stop
    Thread.sleep(5000)
    ClientApp.system.isTerminated shouldBe true
  }

}
