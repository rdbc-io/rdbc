resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.9.2")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.0")
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.0")
addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0")
addSbtPlugin("de.heikoseeberger" % "sbt-header" % "1.8.0")
addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0-RC1")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.4")
