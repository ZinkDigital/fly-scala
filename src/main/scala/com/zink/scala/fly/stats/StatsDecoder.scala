package com.zink.scala.fly.stats

import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import com.zink.scala.fly.stub.StringCodec._

object StatsDecoder {
  val statsOp: Array[Byte] = Array(0xfa.toByte, 0xb1.toByte, 0x0.toByte, 0x09.toByte)
}

class StatsDecoder {

  def stats(sock: Socket): Seq[StatsBean] = getStats(sock, "")

  def stats(sock: Socket, className: String): Option[StatsBean] = getStats(sock, className).headOption

  private def getStats(sock: Socket, className: String): Seq[StatsBean] = {
    val dis = new DataInputStream(sock.getInputStream)
    val dos = new DataOutputStream(sock.getOutputStream)
    dos.write(StatsDecoder.statsOp)
    writeString(dos, className)
    getStats(dis)
  }

  def getStats(dis: DataInputStream): Seq[StatsBean] = for (i ← 0 until dis.readLong.toInt) yield StatsBean(dis)
}
