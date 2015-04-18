import Dependencies._

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation")

lazy val commonSettings = Seq(
  organization := "me.invkrh",
  version := "0.1",
  scalaVersion := "2.11.6",
  resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

lazy val root = project.in(file(".")).
  settings(commonSettings: _*).
  settings(
    name := "cmdchat",
    libraryDependencies ++= rootDeps
  )