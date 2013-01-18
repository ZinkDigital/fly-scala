package com.zink.scala.fly.stub

import com.zink.scala.fly.{ FieldCodec, FlyPrime, FlyAccessException }
import java.io.{ ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream }
import java.util.zip.{ Deflater, Inflater }

class CompressingFieldCodec extends FieldCodec {

  // buffer for coding java objects into byte arrays
  private[this] val bos = new ByteArrayOutputStream(FlyPrime.DEFAULT_BUFFER_SIZE)

  override def writeField(field: AnyRef): Array[Byte] = {
    bos.reset()
    val oos = new ObjectOutputStream(bos)
    oos.writeObject(field)
    oos.flush()
    compress(bos.toByteArray)
  }

  override def readField(fieldBytes: Array[Byte]): Option[AnyRef] = {
    val bis = new ByteArrayInputStream(decompress(fieldBytes))
    val ois = new ObjectInputStream(bis)
    Option(ois.readObject)
  }

  private[this] def compress(toCompress: Array[Byte]): Array[Byte] = {
    try {
      val baos = new ByteArrayOutputStream(FlyPrime.DEFAULT_BUFFER_SIZE)
      val compressor = new Deflater() // move to fields
      compressor.setLevel(Deflater.BEST_COMPRESSION) // move to ctor
      compressor.setInput(toCompress)
      compressor.finish()

      val buffer = new Array[Byte](FlyPrime.DEFAULT_BUFFER_SIZE)
      while (!compressor.finished()) {
        compressor.deflate(buffer)
        baos.write(buffer)
      }
      baos.close()
      baos.toByteArray
    } catch {
      case _: Exception ⇒ throw new FlyAccessException("Compression failed in field codec")
    }
  }

  private[this] def decompress(toDecompress: Array[Byte]): Array[Byte] = {
    try {
      val decompressor = new Inflater()
      decompressor.setInput(toDecompress)
      val bos = new ByteArrayOutputStream(toDecompress.length)
      val buf = new Array[Byte](FlyPrime.DEFAULT_BUFFER_SIZE)
      while (!decompressor.finished()) {
        val count = decompressor.inflate(buf)
        bos.write(buf, 0, count)
        bos.close()
      }
      bos.toByteArray
    } catch {
      case _: Exception ⇒ throw new FlyAccessException("Decompression failed in field codec")
    }
  }
}
