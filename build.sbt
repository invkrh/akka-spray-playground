import Dependencies._

lazy val commonSettings = Seq(
  organization := "me.invkrh",
  version := "0.1",
  scalaVersion := "2.10.4",
  resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "akka-spray-playground"
  ).
  aggregate(commandLineChat).
  settings(
    aggregate in update := false
  ).
  dependsOn(commandLineChat)

lazy val commandLineChat = project.in(file("command-line-chat")).
  settings(commonSettings: _*).
  settings(
    name := "network-chat",
    libraryDependencies ++= networkChatDeps
  )