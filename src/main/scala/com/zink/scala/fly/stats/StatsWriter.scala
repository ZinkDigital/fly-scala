package com.zink.scala.fly.stats

import java.io._
import java.net.{ Socket, UnknownHostException }
import java.text.SimpleDateFormat
import java.util.Date
import com.zink.scala.fly.kit.Logging
import com.zink.scala.fly.stub.StringCodec._
import com.zink.scala.fly.FlyPrime

/**
 *  Stats Writer is a 'low level' tool that can be used with the fly server to
 *  write the output of numerous calls to the stats operation of the space to a
 *  file as defined in the Fly LBI 1.1 and later.
 *
 * @author nigel
 */
class StatsWriter extends Logging {
  private val EMPTY_STRING = ""
  private val FLY_STATS_FILE_EXTENSION = ".fsf"
  private val FLY_STATS_PREFIX = "Fly"

  def main(args: Array[String]) {

    val DEFAULT_PORT = 4396

    val host = if (args.length > 0) {
      args(0)
    } else {
      "localhost" // run locally by default
    }

    val sleep = if (args.length > 1) {
      Integer.parseInt(args(1)).max(1)
    } else {
      100 // do a stats every 1/10 second by default
    }

    try {
      // connection to the fly server
      val socket = new Socket(host, DEFAULT_PORT)
      val dis = new DataInputStream(socket.getInputStream)
      val dos = new DataOutputStream(socket.getOutputStream)

      // open a file to the tmp file system
      val tmpDir = System.getProperty("java.io.tmpdir")
      val sdf = new SimpleDateFormat("yyMMdd-HHmmss")
      val dateStamp = sdf.format(new Date())
      val fileName = FLY_STATS_PREFIX + dateStamp + FLY_STATS_FILE_EXTENSION
      val file = new File(tmpDir, fileName)
      file.createNewFile()
      val fdos = new DataOutputStream(new FileOutputStream(file))

      while (true) {
        // write the stats op
        dos.writeInt(FlyPrime.FLY_HEADER ^ FlyPrime.STATS)
        writeString(dos, EMPTY_STRING)
        val sampleTime = System.currentTimeMillis()

        // sleep while receiving the reply
        Thread.sleep(sleep)

        // read the reply to a buffer
        val replySize = dis.available()
        val buffer = new Array[Byte](replySize.toInt)
        dis.read(buffer)

        // write the reply to a file
        fdos.writeLong(sampleTime)
        fdos.writeInt(replySize)
        fdos.write(buffer)
      }
    } catch {
      case ex: UnknownHostException ⇒
        System.out.println("Could not locate Fly server host " + host)
        logSevere(ex)
      case ex: Exception ⇒
        System.out.println("Error in Fly stats")
        logSevere(ex)
    }
  }
}
