package com.zink.scala.fly.stub

import java.io.{ DataOutputStream, DataInputStream }
import com.zink.scala.fly.FlyAccessException

object StringCodec {

  def writeString(dos: DataOutputStream, string: String) {
    FlyAccessException.wrapExceptionIfThrown {
      dos.writeInt(string.length)
      dos.write(string.getBytes)
    }
  }

  def readString(dis: DataInputStream): String = {
    val length = dis.readInt()
    val bytes = new Array[Byte](length)
    dis.read(bytes)
    new String(bytes)
  } wrappingExceptionsIn_: FlyAccessException

}
