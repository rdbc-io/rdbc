lazy val commonSettings = Seq(
  organization := "io.rdbc",
  version := "0.0.3",
  scalaVersion := "2.11.8",
  scalacOptions ++= Vector(
    "-unchecked",
    "-deprecation",
    "-language:_",
    "-target:jvm-1.8",
    "-encoding", "UTF-8"
  ),
  licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
  bintrayOrganization := Some("rdbc")
)

lazy val rdbc = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    publishArtifact := false,
    bintrayReleaseOnPublish := false
  )
  .aggregate(core)

lazy val core = (project in file("rdbc-core"))
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-core",
    libraryDependencies ++= Vector(
      Library.reactiveStreams
    )
  )
