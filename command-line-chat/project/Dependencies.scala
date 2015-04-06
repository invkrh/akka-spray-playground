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
  val akkaActor   = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaRemote  = "com.typesafe.akka" %% "akka-remote" % akkaVersion
  val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
  val scalaTest   = "org.scalatest" % "scalatest_2.10" % "2.2.4" % "test"

  // Projects
  val rootDeps = Seq(akkaActor, akkaRemote, akkaTestkit, scalaTest)
}
