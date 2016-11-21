import sbt._
import Keys._
import IO._

val packageDist = TaskKey[Unit]("package-dist")
val dist = TaskKey[Unit]("dist")
val flyServerVersion = "fly-2.0"

val tests = Seq(
  "org.mockito" % "mockito-all" % "1.10.19" % "test->default")

val flyJava = "com.flyobjectspace" % "flyjava" % "2.0.4"
val scalatest = "org.scalatest" %% "scalatest" % "3.0.1" % "test"

val buildSettings: Seq[Setting[_]] = Defaults.defaultSettings ++ Seq[Setting[_]](
  organization := "com.flyobjectspace",
  version := "2.2.0-SNAPSHOT",
  scalaVersion := "2.12.0",
  scalacOptions := Seq(
    "-target:jvm-1.8",
    "-language:_",
    // "-Xfatal-warnings",
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
    "-Xlint",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    //"-Ywarn-value-discard",
    "-Xfuture",
    "-Ywarn-unused-import"),


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

lazy val flyScala = Project(
  "FlyScala",
  file("."),
  settings = buildSettings ++ publishSettings ++ Seq(resolvers := Seq(Classpaths.typesafeReleases, Resolver.sonatypeRepo("releases")), libraryDependencies ++= tests ++ Seq(flyJava, scalatest)))
