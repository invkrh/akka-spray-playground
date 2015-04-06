import Dependencies._

lazy val commonSettings = Seq(
  organization := "me.invkrh",
  version := "0.1",
  scalaVersion := "2.10.4",
  resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

lazy val root = project.in(file(".")).
  settings(commonSettings: _*).
  settings(
    name := "command-line-chat",
    libraryDependencies ++= networkChatDeps
  )