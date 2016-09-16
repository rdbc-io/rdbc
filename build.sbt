lazy val commonSettings = Seq(
  organization := "io.rdbc",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.11.8",
  crossScalaVersions := Vector(scalaVersion.value, "2.12.0-RC1"),
  scalacOptions ++= Vector(
    "-unchecked",
    "-deprecation",
    "-language:_",
    "-target:jvm-1.8",
    "-encoding", "UTF-8"
  )
)

lazy val rdbc = (project in file("."))
  .settings(commonSettings: _*)
  .aggregate(core)

lazy val core = (project in file("rdbc-core"))
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-core",
    libraryDependencies ++= Vector(
      Library.reactiveStreams
    )
  )
