package com.zink.scala.fly.stats

import java.io.DataInputStream
import com.zink.scala.fly.stub.StringCodec._

object StatsBean {

  def apply(dis: DataInputStream): StatsBean = {
    val bean = new StatsBean()
    bean.typeName = readString(dis)
    bean.typeChannel = dis.readInt()
    bean.entryCount = dis.readLong()
    bean.totalReads = dis.readLong()
    bean.matchedReads = dis.readLong()
    bean.totalTakes = dis.readLong()
    bean.matchedTakes = dis.readLong()
    bean.writes = dis.readLong()
    bean.notifyWriteTmpls = dis.readLong
    bean.notifyTakeTmpls = dis.readLong
    bean
  }
}

class StatsBean {
  var typeName: String = _
  var typeChannel: Int = _

  var entryCount: Long = _

  var writes: Long = _

  var totalReads: Long = _
  var matchedReads: Long = _

  var totalTakes: Long = _
  var matchedTakes: Long = _

  var notifyTemplates: Long = _
  var notifyWriteTmpls: Long = _
  var notifyTakeTmpls: Long = _
}
