lazy val commonSettings = Seq(
  organization := "io.rdbc",
  version := "0.0.4",
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

lazy val rdbcRoot = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    publishArtifact := false,
    bintrayReleaseOnPublish := false
  )
  .aggregate(rdbcScala, rdbcJava)

lazy val rdbcScala = (project in file("rdbc-scala"))
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-scala",
    libraryDependencies ++= Vector(
      Library.reactiveStreams
    )
  )

lazy val rdbcJava = (project in file("rdbc-java"))
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-java",
    crossPaths := false,
    crossScalaVersions := Vector(scalaVersion.value),
    libraryDependencies ++= Vector(
      Library.reactiveStreams
    )
  )
