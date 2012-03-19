package com.zink.scala.fly.stub

import java.io.InputStream
import java.nio.ByteBuffer

class ByteBufferInputStream(byteBuffer: ByteBuffer) extends InputStream {

  private[this] var bb: ByteBuffer = _

  switchBuffer(byteBuffer)

  def switchBuffer(newByteBuffer: ByteBuffer) {
    synchronized {
      this.bb = newByteBuffer
    }
  }

  def read(): Int = synchronized {
    bb.get & 0xFF
  }

  override def read(b: Array[Byte], off: Int, len: Int): Int = synchronized {
    bb.get(b, off, len)
    len
  }

  override def skip(n: Long): Long = synchronized {
    bb.position((bb.position() + n).toInt)
    n
  }

  override def available(): Int = synchronized { bb.remaining }

  override def markSupported() = true

  override def mark(readAheadLimit: Int) = bb.mark()

  override def reset() {
    synchronized {
      bb.reset()
    }
  }

  /**
   * Doesn't mean anything in this context.
   */
  override def close() {
  }

}

