package com.zink.scala.fly.stub

import com.zink.scala.fly.{FieldCodec, FlyPrime}
import java.io.{ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}
import java.nio.ByteBuffer

class BufferedSerializingFieldCodec extends FieldCodec {

  private[this] val baos = new ByteArrayOutputStream(FlyPrime.DEFAULT_BUFFER_SIZE)
  private[this] val oos = new ObjectOutputStream(baos)
  private[this] val bytes = baos.toByteArray

  private[this] val bbis = new ByteBufferInputStream(ByteBuffer.wrap(bytes))
  private[this] val ois = new ObjectInputStream(bbis)

  override def writeField(field: AnyRef) = {
    baos.reset()
    oos.writeObject(field)
    oos.flush()
    oos.reset()
    baos.toByteArray
  }

  override def readField(fieldBytes: Array[Byte]): Option[AnyRef] = {
    bbis.switchBuffer(ByteBuffer.wrap(fieldBytes))
    Option(ois.readObject)
  }
}
