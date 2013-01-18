package com.zink.scala.fly.kit

import com.zink.scala.fly.FlyAccessException
import com.zink.scala.fly.FlyAccessException._

import java.net.DatagramPacket
import java.net.InetAddress
import java.net.MulticastSocket
import java.util.concurrent.Callable

class MulticastListener(repHandler: FlyRepHandler) extends Callable[Any] {

  private[this] val bytesAddress = Array(232.toByte, 43.toByte, 96.toByte, 232.toByte)
  private[this] val port = 4397
  private[this] val maxBufferSize = 512
  private[this] val multicastAddr = FlyAccessException wrapExceptionIfThrown InetAddress.getByAddress(bytesAddress)

  private[this] var multicastSocket: MulticastSocket = null

  override def call() {
    wrapExceptionIfThrown {

      /* instantiate a MulticastSocket */
      multicastSocket = new MulticastSocket(port)
      multicastSocket.setReuseAddress(true)
      multicastSocket.joinGroup(multicastAddr)

      while (!Thread.currentThread().isInterrupted) {
        /* listen for a new datagram packet */
        val buf = new Array[Byte](maxBufferSize)
        val packet = new DatagramPacket(buf, buf.length)
        multicastSocket.receive(packet)

        // get the tags from the returns
        val tags = new String(packet.getData, 0, packet.getLength)
        repHandler.flyRepReply(FlyServerRep(packet.getAddress, tags.split(" ")))
      }
      multicastSocket.leaveGroup(multicastAddr)
      multicastSocket.close()
    }
  }

  /**
   * socket.recieve blocks even when the thread is signalled terminate
   * so we need to force close the socket from another thread.
   */
  def close() {
    try {
      multicastSocket.leaveGroup(multicastAddr);
    } catch {
      // doesn't ever get here - apparently
      case ex: Exception ⇒ throw new FlyAccessException(ex);
    } finally {
      multicastSocket.close();
    }
  }
}
