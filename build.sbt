import de.heikoseeberger.sbtheader.license.Apache2_0
import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

lazy val commonSettings = Seq(
  organization := "io.rdbc",
  scalaVersion := "2.12.2",
  crossScalaVersions := Vector("2.11.11"),
  scalacOptions ++= Vector(
    "-unchecked",
    "-deprecation",
    "-language:_",
    "-target:jvm-1.8",
    "-encoding", "UTF-8",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard",
    "-Xfuture",
    "-Ywarn-unused"
  ),
  scalacOptions in(Compile, doc) := Vector(
    "-groups",
    "-implicits",
    "-doc-root-content", (resourceDirectory.in(Compile).value / "rootdoc.txt").getAbsolutePath,
    "-doc-footer", "Copyright 2016-2017 Krzysztof Pado",
    "-doc-version", version.value
  ),
  autoAPIMappings := true,
  apiMappings ++= Map(
    scalaInstance.value.libraryJar -> url(s"http://www.scala-lang.org/api/${scalaVersion.value}/")
  ),
  licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
  bintrayOrganization := Some("rdbc"),
  headers := Map(
    "scala" -> Apache2_0("2016", "Krzysztof Pado")
  ),
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    setNextVersion,
    commitNextVersion,
    pushChanges
  ),
  resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"
)

lazy val rdbcRoot = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    publishArtifact := false,
    bintrayReleaseOnPublish := false
  )
  .aggregate(rdbcApiScala, rdbcApiJava, rdbcImplBase, rdbcTypeconv, rdbcUtil, rdbcTests)

lazy val rdbcApiScala = (project in file("rdbc-api-scala"))
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-api-scala",
    libraryDependencies ++= Vector(
      Library.reactiveStreams
    ),
    scalacOptions in(Compile, doc) ++= Vector(
      "-doc-title", "rdbc API"
    ),
    apiURL := Some(url("https://rdbc.io/scala/api"))
  )

lazy val rdbcApiJava = (project in file("rdbc-api-java"))
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-api-java",
    crossPaths := false,
    publishArtifact := scalaVersion.value.startsWith("2.12"),
    bintrayReleaseOnPublish := publishArtifact.value,
    libraryDependencies ++= Vector(
      Library.reactiveStreams
    )
  )

lazy val rdbcImplBase = (project in file("rdbc-implbase"))
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-implbase"
  ).dependsOn(rdbcApiScala, rdbcUtil)

lazy val rdbcTypeconv = (project in file("rdbc-typeconv"))
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-typeconv"
  ).dependsOn(rdbcApiScala)


lazy val rdbcUtil = (project in file("rdbc-util"))
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-util",
    libraryDependencies ++= Vector(
      Library.sourcecode,
      Library.scalaLogging
    )
  )

lazy val rdbcTests = (project in file("rdbc-tests"))
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-tests",
    libraryDependencies ++= Vector(
      Library.scalactic,
      Library.scalatest
    )
  ).dependsOn(rdbcApiScala)
