name := """filmweb"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "3.4.0"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test
libraryDependencies += "org.postgresql" % "postgresql" % "42.7.3"
libraryDependencies += "com.typesafe.slick" %% "slick" % "3.5.0"
libraryDependencies += "com.lihaoyi" %% "upickle" % "3.2.0"
libraryDependencies += filters