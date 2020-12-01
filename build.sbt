name := """play-scala-seed"""
organization := "com.unamur"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "io.reactivex" %% "rxscala" % "0.27.0"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.unamur.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.unamur.binders._"
