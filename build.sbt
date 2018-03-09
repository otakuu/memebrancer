name := """memebrancer"""
organization := "ch.otaku"

version := "1.2-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)
enablePlugins(DockerPlugin)


scalaVersion := "2.12.4"

libraryDependencies ++= Seq(

  guice,
  "com.typesafe.play" %% "play-mailer" % "6.0.0",
  "com.typesafe.play" %% "play-mailer-guice" % "6.0.0"
  
)
