import sbt._
import Keys._
import IO._

object BuildSettings {
  val packageDist = TaskKey[Unit]("package-dist")
  val dist = TaskKey[Unit]("dist")
  val flyServerVersion = "fly-2.0"

  val buildSettings: Seq[Setting[_]] = Defaults.defaultSettings ++ Seq[Setting[_]](
    organization := "com.flyobjectspace",
    version := "2.1.1",
    scalaVersion := "2.10.4",

    packageDist <<= (baseDirectory, crossTarget, version, packageBin in Compile, packageDoc in Compile, packageSrc in Compile, streams) map {
      (theBase, targetDir, theVersion, jarFile, docFile, srcFile, s) =>
        val flyServerZip = theBase / (flyServerVersion + ".zip")
        val serverPath = (targetDir / flyServerVersion) ** "*"
        val lib = theBase / "lib" * "*"
        val libEntries = lib x flat
        val docs = theBase / "docs" * "*"
        val docEntries = docs x flat
        val distribution = theBase / "distribution" * "*"
        val distributionEntries = distribution x flat

        unzip(flyServerZip, targetDir)

        val distPaths = (jarFile +++ docFile +++ srcFile +++ serverPath) x relativeTo(targetDir)
        val distZip = targetDir / ("FlyScala-" + theVersion + ".zip")
        zip(distPaths ++ docEntries ++ libEntries ++ distributionEntries, distZip)
        s.log.info(">>> The distribution is in " + distZip)
    },
    dist <<= Seq(packageBin in Compile, packageDoc in Compile, packageSrc in Compile, packageDist).dependOn)
}

object Dependencies {
  val specs2 = Seq(
    "org.specs2" %% "specs2" % "2.3.12" % "test",
    "org.mockito" % "mockito-all" % "1.9.5" % "test->default")

  val flyJava = "com.flyobjectspace" % "flyjava" % "2.0.2"
  val pegdown = "org.pegdown" % "pegdown" % "1.2.1" % "test"
  val junit = "junit" % "junit" % "4.11" % "test"
  val scalaActors = "org.scala-lang" % "scala-actors" % "2.10.4"

}

/* see ubli and http://www.cakesolutions.net/teamblogs/2012/01/28/publishing-sbt-projects-to-nexus/
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
    pomIncludeRepository := { _ => false},
    publishTo <<= version { v: String =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },

    pomExtra :=
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
        </mailingLists>)
}

object FlyScalaBuild extends Build {

  import BuildSettings._
  import Dependencies._
  import Publishing._

  lazy val flyScala = Project(
    "FlyScala",
    file("."),
    settings = buildSettings ++ publishSettings ++ Seq(resolvers := Seq(Classpaths.typesafeReleases, Resolver.sonatypeRepo("releases")), libraryDependencies ++= specs2 ++ Seq(flyJava, junit, pegdown, scalaActors)))
}
