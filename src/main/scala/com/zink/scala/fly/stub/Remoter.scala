package com.zink.scala.fly.stub

import com.zink.scala.fly.FlyPrime
import com.zink.scala.fly.FlyAccessException

import scala.actors.Actor
import java.io.DataInputStream
import java.net.{ InetAddress, Socket }

import java.util.concurrent.LinkedBlockingQueue

/**
 *  The comms with the server.
 */
class Remoter(socket: Socket) {

  def this(hostAddr: InetAddress, port: Int) = this(new Socket(hostAddr, port))

  def this(hostAddr: String, port: Int) = this(new Socket(hostAddr, port))

  private[this] val os = socket.getOutputStream
  private[this] val dataInputStream = new DataInputStream(socket.getInputStream)

  private[this] val replyQueue = new LinkedBlockingQueue[Long](1)

  // mark a message is still in the dis so don't read it yet.
  private[this] var messagePending = false
  private[this] var notifyDispatcher: NotifyMessageDispatcher = _

  new MessageListener().start()

  def setNotifyMessageDispatcher(nmd: NotifyMessageDispatcher) {
    synchronized {
      if (notifyDispatcher == null) {
        notifyDispatcher = nmd
      }
    }
  }

  /**
   * @return the first long on the stream.
   */
  def sendOperation(method: Array[Byte]): Long = try {
    os.write(method)
    os.flush()
    replyQueue.take()
  } catch {
    case ex: Exception ⇒ throw new FlyAccessException(ex)
  }

  private[this] def setMessagePending() = synchronized(messagePending = true)

  def setMessageComplete() = synchronized(messagePending = false)

  def addNotifyDetails(notifyToken: Long, actor: Actor, clss: Class[AnyRef]) = notifyDispatcher.registerHandler(notifyToken, actor, clss);

  def readFully(array: Array[Byte]) {
    dataInputStream.readFully(array)
  }

  def readLong(): Long = dataInputStream.readLong
  
  def readInt(): Int = dataInputStream.readInt

  def readString(): String = StringCodec.readString(dataInputStream)

  private[this] class MessageListener extends Thread {
    setName("MessageListener")
    setDaemon(true)

    override def run() {
      messagePending = false
      while (!isInterrupted) {

        // if the codec is still reading the message wait until the
        // dis is cleared before getting for the next message
        while (messagePending) Thread.`yield`()

        val firstLong = dataInputStream.readLong()

        if (firstLong == FlyPrime.NOTIFY_SIMPLE || firstLong == FlyPrime.NOTIFY_WITH_OBJECT) {
          notifyDispatcher.decodeAndQueue(firstLong, dataInputStream);
        } else {
          setMessagePending()
          replyQueue.put(firstLong)
        }
      }
    }
  }
}
