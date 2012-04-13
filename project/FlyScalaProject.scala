import sbt._
import Keys._
import java.io.File
import scala.io.Source
import IO._

object BuildSettings {
  val packageDist = TaskKey[Unit]("package-dist")
  val dist = TaskKey[Unit]("dist")
  val flyServerVersion = "2.0-BETA"

  val buildSettings: Seq[Setting[_]] = Defaults.defaultSettings ++ Seq[Setting[_]](
    organization := "com.flyobjectspace",
    version := "2.0.0-SNAPSHOT",
    scalaVersion := "2.9.1",
    javaOptions ++= Seq("-Xmx256m", "-Xss4m", "-server"),

    testOptions in Test ++= Seq(Tests.Argument("junitxml", "html", "console")),

    packageDist <<= (baseDirectory, crossTarget, version, packageBin in Compile, packageDoc in Compile, packageSrc in Compile) map {
      (theBase, targetDir, theVersion, jarFile, docFile, srcFile) ⇒

        val flyArchiveProject = new File(theBase.asFile.getParentFile, "FlyArchive")
        val flyServerZip = new File(flyArchiveProject, "src/Fly-" + flyServerVersion + ".zip")
        val serverPath = (targetDir / "fly") ** "*"
        val docs = (theBase / "docs" * "*")
        val distribution = (theBase / "distribution" * "*")
        val docEntries = docs x flat
        val distributionEntries = distribution x flat

        unzip(flyServerZip, targetDir)

        val distPaths = (jarFile +++ docFile +++ srcFile +++ serverPath) x relativeTo(targetDir)
        zip(distPaths ++ docEntries ++ distributionEntries, (targetDir / ("FlyScala-" + theVersion + ".zip")))
    },
    dist <<= Seq(packageBin in Compile, packageDoc in Compile, packageSrc in Compile, packageDist).dependOn)
}

object Dependencies {
  val specs2 = Seq(
    "org.specs2" %% "specs2" % "1.8.2" % "test->default",
    "org.mockito" % "mockito-all" % "1.9.0" % "test->default")

  val pegdown = "org.pegdown" % "pegdown" % "1.1.0" % "test"
  val junit = "junit" % "junit" % "4.10" % "test"

}

/* see http://www.scala-sbt.org/using_sonatype.html and http://www.cakesolutions.net/teamblogs/2012/01/28/publishing-sbt-projects-to-nexus/
 * Instructions from sonatype: https://issues.sonatype.org/browse/OSSRH-2841?focusedCommentId=150049#comment-150049
 * Deploy snapshot artifacts into repository https://oss.sonatype.org/content/repositories/snapshots
 * Deploy release artifacts into the staging repository https://oss.sonatype.org/service/local/staging/deploy/maven2
 * Promote staged artifacts into repository 'Releases'
 * Download snapshot and release artifacts from group https://oss.sonatype.org/content/groups/public
 * Download snapshot, release and staged artifacts from staging group https://oss.sonatype.org/content/groups/staging
 */
object Publishing {

  def publishSettings: Seq[Setting[_]] = Seq(
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ ⇒ false },

    publishTo <<= version { v: String ⇒
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },

    pomExtra := (
      <licenses>
        <license>
          <name>MIT License</name>
          <url>http://www.opensource.org/licenses/mit-license/</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:fly-object-space/fly-scala.git</url>
        <connection>scm:git:git@github.com:fly-object-space/fly-scala.git</connection>
      </scm>
      <description>
        Fly Scala is a scala client library for working with a Fly Server.
      Fly is an object space server that is specifically written to provide
      lightweight object based messaging between computers running on a network.
      This distribution does not contain the server, please go to
      http://www.flyobjectspace.com/ for the server that matches the version
      of Fly Scala you want to use.
      </description>
      <url>http://www.flyobjectspace.com</url>
      <developers>
        <developer>
          <id>cjw</id>
          <name>Channing Walton</name>
          <email>channing [dot] walton [at] underscoreconsulting [dot] com</email>
          <organization>Underscore Consulting Ltd</organization>
        </developer>
        <developer>
          <id>nw</id>
          <name>Nigel Warren</name>
          <organization>Zink Digital Ltd</organization>
        </developer>
      </developers>
      <mailingLists>
        <mailingList>
          <name>User and Developer Discussion List</name>
          <archive>http://groups.google.com/group/flyobjectspace</archive>
          <post>flyobjectspace@googlegroups.com</post>
          <subscribe>flyobjectspace+subscribe@googlegroups.com</subscribe>
          <unsubscribe>flyobjectspace+unsubscribe@googlegroups.com</unsubscribe>
        </mailingList>
      </mailingLists>))
}

object FlyScalaBuild extends Build {

  import BuildSettings._
  import Dependencies._
  import Publishing._

  lazy val flyScala = Project(
    "FlyScala",
    file("."),
    settings = buildSettings ++ publishSettings ++ Seq(resolvers := Seq(Classpaths.typesafeResolver), libraryDependencies ++= specs2 ++ Seq(junit, pegdown)))
}
