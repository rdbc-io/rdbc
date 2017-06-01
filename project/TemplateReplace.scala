import java.io.PrintWriter

import sbt.Keys.baseDirectory
import sbt._

import scala.io.Source

object TemplateReplace extends AutoPlugin {

  object autoImport {
    val mkdocsVariables = settingKey[Map[String, String]]("Variables that will be replaced with values")
    val mkdocs = taskKey[Unit]("Generates mkdocs documentation")
  }

  import autoImport._

  private def processDir(dir: File, variables: Map[String, String]): Unit = {
    dir.listFiles().foreach { f =>
      if (f.isDirectory) {
        processDir(f, variables)
      }
    }
    dir.listFiles().foreach { f =>
      if (f.isFile && f.name.endsWith(".html")) {
        val substituted = Source.fromFile(f, "UTF-8").getLines().map { line =>
          var newLine = line
          variables.foreach { case (variable, value) =>
            newLine = newLine.replaceAll(s"\\{\\{$variable\\}\\}", value)
          }
          newLine
        }.reduce(_ + "\n" + _) + "\n"

        val pw = new PrintWriter(f, "UTF-8")
        pw.write(substituted)
        pw.close()
      }
    }
  }

  override def projectSettings = Seq(
    mkdocs := {
      Process(Seq("mkdocs", "build", "--clean", "-q"), baseDirectory.value).!!
      processDir(baseDirectory.value / "target", mkdocsVariables.value)
    }
  )
}
