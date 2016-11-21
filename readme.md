# Fly Object Space For Scala


Welcome to Fly for Scala!

**Fly** is an Object Space server that is specifically written to provide
lightweight object based messaging between computers running on a network.
The server is written in C, with prebuilt binaries available for Linux,
OSX, Solaris and Win32.

Before you start you need to make sure that have a version of Java installed
and running on your machine. Type -

```
> java -version
```

into a command prompt or shell. If this fails you will need to download a
recent version of Java SE (1.6 or above for Scala version < 2.12, 1.8 for Scala >= 2.12)
from www.java.com and install this onto your machine.


Getting Fly-Scala
---------------

Get the fly binary, which is part of the fly-java project, from https://github.com/fly-object-space/fly-java/releases

For _Scala 2.12.x_ In SBT do this (which will pull flyjava from the mavens):

```scala
libraryDependencies ++= Seq("com.flyobjectspace" %% "flyscala" % "2.2.0-SNAPSHOT")
```

For _Scala 2.11.x_ In SBT do this:

```scala
libraryDependencies ++= Seq("com.flyobjectspace" %% "flyscala" % "2.1.6")
```

For _Scala 2.10.x_, do this:

```scala
libraryDependencies ++= Seq("com.flyobjectspace" %% "flyscala" % "2.1.5")
```

In windows double click the startFly.bat file from the windows explorer and
then double click the runExample.bat file. In OSX or Linux, type ...

```
> cd fly
> .\startFly.bat
> .\runExample.bat
```

in a windows command prompt.

On unix systems the startFly.sh script is set up to run the version of
fly for the host platform. See the comments in the script if you want to
run the fly server directly.

```
% cd fly
% sh startFly.sh
% sh runExample.sh
```

In either case, if this successful you will see something like this -

```
      >      
    <----    
  -------->  
    <----       Fly Server (c) MMVI Zink Digital Ltd.
      >       Ver 2.0 : LBI 2.0 : Non Commercial License.

Fly Server started on port 4396
```

and then some output from the example code which writes and takes 1000 example
objects to and from the space server. To write more or less objects, vary the
final parameter, or try running a number of example clients in parallel.

If you want to see the scala source for the WriteTake example look in the src
directory. There are many examples in here of how to use the server via the
scala bindings.

Enjoy using Fly!
