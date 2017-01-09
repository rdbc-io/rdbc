import sbt._

object Library {
  private val scalatestVersion = "3.0.4"

  val reactiveStreams = "org.reactivestreams" % "reactive-streams" % "1.0.1"
  val sourcecode = "com.lihaoyi" %% "sourcecode" % "0.1.4"
  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"
  val scalactic = "org.scalactic" %% "scalactic" % scalatestVersion
  val scalamock = "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0"
  val scalatest = "org.scalatest" %% "scalatest" % scalatestVersion
  val reactiveStreamsTck = "org.reactivestreams" % "reactive-streams-tck" % "1.0.1"
  val akkaStream = "com.typesafe.akka" %% "akka-stream" % "2.5.6"
  val java8Compat = "org.scala-lang.modules" %% "scala-java8-compat" % "0.8.0"
}
