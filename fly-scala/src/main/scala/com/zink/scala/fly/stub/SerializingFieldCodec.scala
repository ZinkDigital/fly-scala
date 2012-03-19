package com.zink.scala.fly.stub

import com.zink.scala.fly.{ FieldCodec, FlyPrime }
import com.zink.scala.fly.FlyAccessException
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class SerializingFieldCodec extends FieldCodec {

  // buffer for coding objects into byte arrays
  private[this] val bos = new ByteArrayOutputStream(FlyPrime.DEFAULT_BUFFER_SIZE)

  override def writeField(field: AnyRef): Array[Byte] = try {
    bos.reset()
    val oos = new ObjectOutputStream(bos)
    oos.writeObject(field)
    oos.flush()
    bos.toByteArray
  } catch {
    case ex: Exception ⇒ throw new FlyAccessException(ex)
  }

  override def readField(fieldBytes: Array[Byte]): Option[AnyRef] = try {
    Option(new ObjectInputStream(new ByteArrayInputStream(fieldBytes)).readObject)
  } catch {
    case ex: Exception ⇒ throw new FlyAccessException(ex)
  }
}
