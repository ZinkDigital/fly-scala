Getting Started
===============

run the sbt script (from https://github.com/paulp/sbt-extras) which will get a recent version of sbt and start it.


Testing
=======
The tests require a local fly server.

In sbt, type ~test for continuous testing as you touch and save files

Publishing to Sonatype
==========================

Deploy snapshot artifacts into repository https://oss.sonatype.org/content/repositories/snapshots
Deploy release artifacts into the staging repository https://oss.sonatype.org/service/local/staging/deploy/maven2
Promote staged artifacts into repository 'Releases'
Download snapshot and release artifacts from group https://oss.sonatype.org/content/groups/public
Download snapshot, release and staged artifacts from staging group https://oss.sonatype.org/content/groups/staging

see http://www.scala-sbt.org/using_sonatype.html

Change your version number in project/build.properties, appending -SNAPSHOT if you want it to go to the snapshot repo.

In sbt, run 'publish'

Distribution
============

In sbt type 'dist' and it will build the dist.zip into target/[scala_version]/fly-[fly version].zip

The dist task expects to find the FlyArchive directory at the same level as FlyScala. It needs this to
get the server to include in the distribution.

Modifying the Project
=====================
Our sbt project file is project/FlyScalaProject.scala (which is just a Scala class).


