import sbt._

object Versions {
  val scalatest = "3.0.2"
}

object Library {
  val reactiveStreams = "org.reactivestreams" % "reactive-streams" % "1.0.0"
  val sourcecode = "com.lihaoyi" %% "sourcecode" % "0.1.3"
  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
  val scalactic = "org.scalactic" %% "scalactic" % Versions.scalatest
  val scalatest = "org.scalatest" %% "scalatest" % Versions.scalatest
}
