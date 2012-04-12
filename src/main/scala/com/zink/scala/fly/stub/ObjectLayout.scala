package com.zink.scala.fly.stub

import com.zink.scala.fly.FlyAccessException
import com.zink.scala.fly.stub.StringCodec._
import com.zink.scala.fly.FieldCodec
import java.io.DataOutputStream

object ObjectLayout {

  def apply(remoter: Remoter, ignored: Int, fieldCodec: FieldCodec): ObjectLayout = {
    val className = remoter.readString()
    val channel = remoter.readInt()
    val size = remoter.readInt()

    val infos = for {
      i ← 0 until size
      tipe = remoter.readString()
      name = remoter.readString()
    } yield FieldInfo(tipe, name)

    new ObjectLayout(className, channel, infos.toList, fieldCodec)
  } wrappingExceptionsIn_: FlyAccessException

  def apply(obj: AnyRef, fieldCodec: FieldCodec): ObjectLayout = {
    val fields = obj.getClass.getDeclaredFields
    val infos = fields.filter(FieldFilter(_)).map(new FieldInfo(_))
    new ObjectLayout(obj.getClass.getName, 0, infos, fieldCodec)
  }
}

class ObjectLayout(className: String, var channel: Int, val infos: Seq[FieldInfo], fieldCodec: FieldCodec) {

  def write(dos: DataOutputStream) {
    {
      writeString(dos, className)
      dos.writeInt(channel)
      dos.writeInt(infos.size)

      infos.foreach(info ⇒ {
        writeString(dos, info.theType)
        writeString(dos, info.name)
      })

    } wrappingExceptionsIn_: FlyAccessException
  }

  override def toString: String = {
    var str = "Layout for :" + className + "\n"
    str += "Type channel :" + channel.toString + "\n"
    for (info ← infos) {
      str += "Type: " + info.theType + "\tName: " + info.name + "\n"
    }
    str
  }

}
