import java.time.LocalDate

import sbt.Keys._
import sbt._
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations._

object Settings {

  object Copyright {
    val startYear = 2016
    val years: String = startYear + {
      val currentYear = LocalDate.now().getYear
      if(currentYear == startYear) ""
      else " " + currentYear
    }
  }

  val compilationConf = Vector(
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
    javacOptions ++= Vector(
      "-source", "1.8",
      "-target", "1.8"
    )
  )

  val scaladocConf = Vector(
    scalacOptions in(Compile, doc) := Vector(
      "-groups",
      "-implicits",
      "-doc-root-content", (resourceDirectory.in(Compile).value / "rootdoc.txt").getAbsolutePath,
      "-doc-footer", s"Copyright ${Copyright.years} ${organizationName.value}",
      "-doc-version", version.value
    ),
    autoAPIMappings := true,
    apiMappings ++= Map(
      scalaInstance.value.libraryJar -> url(s"http://www.scala-lang.org/api/${scalaVersion.value}/")
    )
  )

  val publishConf = Vector(
    publishMavenStyle := true,
    publishArtifact in Test := false,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    releaseProcess := Vector(
      checkSnapshotDependencies,
      inquireVersions,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      setNextVersion,
      commitNextVersion,
      pushChanges
    )
  )

  val developersConf = Vector(
    developers := List(
      Developer(
        id = "povder",
        name = "Krzysztof Pado",
        email = "povder@gmail.com",
        url = url("https://github.com/povder")
      )
    )
  )
}
