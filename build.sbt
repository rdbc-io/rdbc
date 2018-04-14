import Settings._

import scala.Console._

shellPrompt.in(ThisBuild) := (state => s"${CYAN}project:$GREEN${Project.extract(state).currentRef.project}$RESET> ")

lazy val displayJavaVersion = taskKey[Unit]("Displays Java version")
displayJavaVersion := { println(s"Running sbt with Java ${System.getProperty("java.version")}") }

lazy val commonSettings = Vector(
  organization := "io.rdbc",
  organizationName := "rdbc contributors",
  scalaVersion := "2.12.4",
  crossScalaVersions := Vector(scalaVersion.value, "2.11.12"),

  licenses := Vector(
    "Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.html")
  ),
  startYear := Some(Copyright.startYear),

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
  .aggregate(rdbcApiScala, rdbcApiJava, rdbcImplBase, rdbcTypeconv, rdbcUtil, rdbcTck, rdbcJavaAdapter)

lazy val rdbcApiScala = (project in file("rdbc-api-scala"))
  .enablePlugins(BuildInfoPlugin)
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-api-scala",
    libraryDependencies ++= Vector(
      Library.reactiveStreams,
      Library.scalatest % Test,
      Library.scalamock % Test
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
    crossPaths := false,
    autoScalaLibrary := false,
    crossScalaVersions := Vector.empty,
    libraryDependencies ++= Vector(
      Library.reactiveStreams
    )
  )

lazy val rdbcJavaAdapter = (project in file("rdbc-java-adapter"))
  .enablePlugins(BuildInfoPlugin)
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-java-adapter",
    libraryDependencies ++= Vector(
      Library.java8Compat
    ),
    buildInfoPackage := "io.rdbc.jadapter",
  ).dependsOn(rdbcApiJava, rdbcApiScala)

lazy val rdbcImplBase = (project in file("rdbc-implbase"))
  .enablePlugins(BuildInfoPlugin)
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-implbase",
    libraryDependencies ++= Vector(
      Library.scalatest % Test,
      Library.scalamock % Test,
      Library.reactiveStreamsTck % Test,
      Library.akkaStream % Test
    ),
    buildInfoPackage := "io.rdbc.implbase"
  ).dependsOn(rdbcApiScala, rdbcUtil)

lazy val rdbcTypeconv = (project in file("rdbc-typeconv"))
  .enablePlugins(BuildInfoPlugin)
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-typeconv",
    libraryDependencies ++= Vector(
      Library.scalatest % Test
    ),
    buildInfoPackage := "io.rdbc.typeconv"
  ).dependsOn(rdbcApiScala)


lazy val rdbcUtil = (project in file("rdbc-util"))
  .enablePlugins(BuildInfoPlugin)
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-util",
    libraryDependencies ++= Vector(
      Library.sourcecode,
      Library.scalaLogging,
      Library.scalatest % Test,
      Library.scalamock % Test
    ),
    buildInfoPackage := "io.rdbc.util"
  )

lazy val rdbcTck = (project in file("rdbc-tck"))
  .enablePlugins(BuildInfoPlugin)
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-tck",
    libraryDependencies ++= Vector(
      Library.scalactic,
      Library.scalatest,
      Library.reactiveStreamsTck,
      Library.akkaStream
    ),
    buildInfoPackage := "io.rdbc.tck",
    scalacOptions -= "-Ywarn-value-discard"
  ).dependsOn(rdbcApiScala)

lazy val rdbcDoc = (project in file("rdbc-doc"))
  .enablePlugins(TemplateReplace)
  .settings(
    publishArtifact := false,
    mkdocsVariables := Map(
      "version" -> version.value,
      "scaladocRoot" -> ("https://javadoc.io/page/" + organization.in(rdbcApiScala).value + "/" + name.in(rdbcApiScala).value + "_2.12/" + version.value)
    )
  )
