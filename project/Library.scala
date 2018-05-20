import sbt._

object Library {
  private val scalatestVersion = "3.0.5"
  private val reactiveStreamsVersion = "1.0.2"

  val reactiveStreams = "org.reactivestreams" % "reactive-streams" % reactiveStreamsVersion
  val sourcecode = "com.lihaoyi" %% "sourcecode" % "0.1.4"
  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"
  val scalactic = "org.scalactic" %% "scalactic" % scalatestVersion
  val scalamock = "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0"
  val scalatest = "org.scalatest" %% "scalatest" % scalatestVersion
  val reactiveStreamsTck = "org.reactivestreams" % "reactive-streams-tck" % reactiveStreamsVersion
  val akkaStream = "com.typesafe.akka" %% "akka-stream" % "2.5.12"
  val java8Compat = "org.scala-lang.modules" %% "scala-java8-compat" % "0.9.0"
  val immutables = "org.immutables" % "value" % "2.5.5"
}
