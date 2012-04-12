package com.zink.scala.fly.stub

import com.zink.scala.fly.FieldCodec
import java.io.DataOutputStream

/**
 *  Read and write objects to and from streams.
 */
class ObjectCodec(remoter: Remoter, fieldCodec: FieldCodec) {

  def writeObject(dos: DataOutputStream, obj: AnyRef) {
    Option(obj) match {
      case None ⇒ dos.writeLong(0)
      case Some(thing) ⇒
        val objBytes = fieldCodec.writeField(thing)
        dos.writeLong(objBytes.length)
        dos.write(objBytes)
    }
  }

  def readObject(): Option[AnyRef] = remoter.readLong() match {
    case 0 ⇒ None
    case x ⇒
      val fieldBytes = new Array[Byte](x.asInstanceOf[Int])
      remoter.readFully(fieldBytes)
      fieldCodec.readField(fieldBytes)
  }
}
