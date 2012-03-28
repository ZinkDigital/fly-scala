package com.zink.scala.fly.stats

import java.io._

object StatsReader {
  def main(args: Array[String]) {

    if (args.length == 0) System.err.println("You must supply an fsf file as an argument")

    val f = new FileInputStream(new File(args(0)))
    val dis = new DataInputStream(f)

    val decoder = new StatsDecoder()

    var time = dis.readLong()
    while (true) {
      val size = dis.readInt()
      val beans = decoder.getStats(dis)
      StatsPrinter.writeStats(beans)
      System.out.println("--------------")
      val nextTime = dis.readLong()
      Thread.sleep(nextTime - time)
      time = nextTime
    }
  }
}
