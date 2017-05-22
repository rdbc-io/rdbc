import Settings._
import de.heikoseeberger.sbtheader.license.Apache2_0
import scala.Console._

shellPrompt.in(ThisBuild) := (state => s"${CYAN}project:$GREEN${Project.extract(state).currentRef.project}$RESET> ")

lazy val commonSettings = Vector(
  organization := "io.rdbc",
  scalaVersion := "2.12.2",
  crossScalaVersions := Vector("2.11.11"),

  licenses := Vector(
    "Apache License, Version 2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.html")
  ),
  headers := Map(
    "scala" -> Apache2_0(Copyright.years, Copyright.holder)
  ),

  homepage := Some(url("https://github.com/rdbc-io/rdbc")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/rdbc-io/rdbc"),
      "scm:git@github.com:rdbc-io/rdbc.git"
    )
  ),

  buildInfoKeys := Vector(version, scalaVersion, git.gitHeadCommit, BuildInfoKey.action("buildTime") {
    java.time.Instant.now()
  }),

  scalastyleFailOnError := true
) ++ compilationConf ++ scaladocConf ++ developersConf ++ publishConf

lazy val rdbcRoot = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    publishArtifact := false
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
    buildInfoPackage := "io.rdbc.sapi"
  )

lazy val rdbcApiJava = (project in file("rdbc-api-java"))
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-api-java",
    libraryDependencies ++= Vector(
      Library.reactiveStreams
    ),
    buildInfoPackage := "io.rdbc.japi"
  )

lazy val rdbcImplBase = (project in file("rdbc-implbase"))
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-implbase",
    buildInfoPackage := "io.rdbc.implbase"
  ).dependsOn(rdbcApiScala, rdbcUtil)

lazy val rdbcTypeconv = (project in file("rdbc-typeconv"))
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-typeconv",
    buildInfoPackage := "io.rdbc.typeconv"
  ).dependsOn(rdbcApiScala)


lazy val rdbcUtil = (project in file("rdbc-util"))
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-util",
    libraryDependencies ++= Vector(
      Library.sourcecode,
      Library.scalaLogging
    ),
    buildInfoPackage := "io.rdbc.util"
  )

lazy val rdbcTests = (project in file("rdbc-tests"))
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-tests",
    libraryDependencies ++= Vector(
      Library.scalactic,
      Library.scalatest,
      Library.reactiveStreamsTck,
      Library.akkaStream
    ),
    buildInfoPackage := "io.rdbc.test",
    scalacOptions -= "-Ywarn-value-discard"
  ).dependsOn(rdbcApiScala)
