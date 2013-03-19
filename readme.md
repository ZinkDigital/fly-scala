Getting Started
===============

You need sbt. I recommend you get sbt.sh from https://github.com/paulp/sbt-extras.

Testing
=======
The tests require a local fly server.

In sbt, type ~test for continuous testing as you touch and save files

Publishing to Sonatype
======================

Make sure you have the sonatype.sbt file with credentials in ~/.sbt/<sbt-version> or in ~/.sbt. It depends on your set up.
The contents of the file should be:
  credentials += Credentials("Sonatype Nexus Repository Manager","oss.sonatype.org","username","password")

see http://www.scala-sbt.org/using_sonatype.html for more.

Cross build by prefixing stuff with '+', eg + publish, + package

Deploy snapshot artifacts into repository https://oss.sonatype.org/content/repositories/snapshots
Deploy release artifacts into the staging repository https://oss.sonatype.org/service/local/staging/deploy/maven2
Promote staged artifacts into repository 'Releases'
Download snapshot and release artifacts from group https://oss.sonatype.org/content/groups/public
Download snapshot, release and staged artifacts from staging group https://oss.sonatype.org/content/groups/staging

In sbt, run 'publish'

Distribution
============

In sbt type 'dist' and it will build the dist.zip into target/[scala_version]/fly-[fly version].zip

The dist task expects to find the FlyArchive directory at the same level as FlyScala. It needs this to
get the server to include in the distribution.

Modifying the Project
=====================
Our sbt project file is project/FlyScalaProject.scala (which is just a Scala class).


