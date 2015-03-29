/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 3/29/15
 * Time: 9:50 PM
 */

import sbt._

object Dependencies {
  // Versions
  lazy val akkaVersion = "2.3.9"

  // Libraries
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion

  // Projects
  val networkChatDeps = Seq(akkaActor)
}
