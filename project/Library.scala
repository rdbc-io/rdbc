import sbt._

object Library {
  private val scalatestVersion = "3.0.2"

  val reactiveStreams = "org.reactivestreams" % "reactive-streams" % "1.0.0"
  val sourcecode = "com.lihaoyi" %% "sourcecode" % "0.1.3"
  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
  val scalactic = "org.scalactic" %% "scalactic" % scalatestVersion
  val scalatest = "org.scalatest" %% "scalatest" % scalatestVersion
  val reactiveStreamsTck = "org.reactivestreams" % "reactive-streams-tck" % "1.0.0"
  val akkaStream = "com.typesafe.akka" %% "akka-stream" % "2.4.18"
}
