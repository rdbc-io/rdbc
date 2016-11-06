import de.heikoseeberger.sbtheader.HeaderPattern
import de.heikoseeberger.sbtheader.license.Apache2_0

lazy val commonSettings = Seq(
  organization := "io.rdbc",
  version := "0.0.11",
  scalaVersion := "2.12.0",
  crossScalaVersions := Seq("2.11.8"),
  scalacOptions ++= Vector(
    "-unchecked",
    "-deprecation",
    "-language:_",
    "-target:jvm-1.8",
    "-encoding", "UTF-8"
  ),
  autoAPIMappings := true,
  apiMappings ++= Map(
    scalaInstance.value.libraryJar -> url(s"http://www.scala-lang.org/api/${scalaVersion.value}/")
  ),
  licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
  bintrayOrganization := Some("rdbc"),
  headers := Map(
    "scala" -> Apache2_0("2016", "Krzysztof Pado")
  )
)

lazy val rdbcRoot = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    publishArtifact := false,
    bintrayReleaseOnPublish := false
  )
  .aggregate(rdbcApiScala, rdbcApiJava, rdbcImplBase, rdbcTypeconv)

lazy val rdbcApiScala = (project in file("rdbc-api-scala"))
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-api-scala",
    libraryDependencies ++= Vector(
      Library.reactiveStreams
    ),
    apiURL := Some(url("https://rdbc.io/scala/api"))
  )

lazy val rdbcApiJava = (project in file("rdbc-api-java"))
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-api-java",
    crossPaths := false,
    crossScalaVersions := Vector(scalaVersion.value),
    libraryDependencies ++= Vector(
      Library.reactiveStreams
    )
  )

lazy val rdbcImplBase = (project in file("rdbc-implbase"))
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-implbase"
  ).dependsOn(rdbcApiScala)

lazy val rdbcTypeconv = (project in file("rdbc-typeconv"))
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-typeconv"
  ).dependsOn(rdbcApiScala)
