package com.zink.scala.fly.stub

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.net.InetAddress
import java.nio.ByteBuffer
import java.util.UUID

import scala.Option.option2Iterable
import scala.actors.Actor

import com.zink.scala.fly.kit.Logging
import com.zink.scala.fly.FieldCodec
import com.zink.scala.fly.FlyAccessException
import com.zink.scala.fly.FlyPrime

/**
 * Method Codec
 *
 * This is where methods on the implementing interface get converted into into
 * the binary format calls. The binary formats of the methods are then passed
 * to the remoter to carry them over to the fly host.
 *
 * This does not get involved in the evolution of Entries - that happens in the
 * VersionLink which is twined' with the MethodCodec.
 *
 * A non evolvable stub could easily be made by excluding the VersionLink and
 * implementing the evolution preamble to the space directly in this class.
 * Performance, of course, could be improved in this case.
 *
 */
class MethodCodec(remoter: Remoter, fieldCodec: FieldCodec) extends Logging {

  def this(hostname: String, fieldCodec: FieldCodec) = this(new Remoter(hostname, FlyPrime.FLY_PORT), fieldCodec)
  def this(address: InetAddress, fieldCodec: FieldCodec) = this(new Remoter(address, FlyPrime.FLY_PORT), fieldCodec)

  private[this] val typeChain = new TypeChain(remoter, fieldCodec)
  private[this] val nmd = new NotifyMessageDispatcher(typeChain)
  private[this] val bos = new ByteArrayOutputStream(FlyPrime.DEFAULT_BUFFER_SIZE)
  private[this] val dos = new DataOutputStream(bos)
  private[this] val inBuffer = ByteBuffer.allocateDirect(FlyPrime.DEFAULT_BUFFER_SIZE)

  remoter.setNotifyMessageDispatcher(nmd)

  def read[T <: AnyRef](template: T, timeout: Long): Option[T] = retrieve(template, timeout, FlyPrime.FLY_HEADER ^ FlyPrime.READ)

  def take[T <: AnyRef](template: T, timeout: Long): Option[T] = retrieve(template, timeout, FlyPrime.FLY_HEADER ^ FlyPrime.TAKE)

  private[this] def theClass[T <: AnyRef](obj: T) = obj.getClass.asInstanceOf[Class[T]]

  private[this] def retrieve[T <: AnyRef](template: T, timeout: Long, methodCodecHeader: Int): Option[T] = remoterOp {
    writeRetrieveHeader(template, methodCodecHeader)
    dos.writeLong(timeout)
    val size = remoter.sendOperation(bos.toByteArray)
    if (size <= 0) None else typeChain.readObject(size, theClass(template))
  }

  private[this] def writeRetrieveHeader[T <: AnyRef](template: T, methodCodecHeader: Int) {
    bos.reset()
    dos.writeInt(methodCodecHeader)
    dos.writeInt(typeChain.getChannel(template))
    typeChain.writeObject(dos, template)
  }

  def snapshot(template: AnyRef): AnyRef = template

  def write(obj: AnyRef, lease: Long): Long = remoterOp {
    bos.reset()
    dos.writeInt(FlyPrime.FLY_HEADER ^ FlyPrime.WRITE)
    dos.writeInt(typeChain.getChannel(obj))
    typeChain.writeIDObject(dos, obj, Some(UUID.randomUUID()))
    dos.writeLong(lease)
    remoter.sendOperation(bos.toByteArray)
  }

  def readMany[T <: AnyRef](template: T, matchLimit: Long, ignore: Long): Iterable[T] = remoterOp {
    writeRetrieveHeader(template, FlyPrime.FLY_HEADER ^ FlyPrime.READ_MANY)
    dos.writeLong(matchLimit)
    dos.writeLong(ignore)
    retrieveMany(template)
  }

  def takeMany[T <: AnyRef](template: T, matchLimit: Long): Iterable[T] = remoterOp {
    writeRetrieveHeader(template, FlyPrime.FLY_HEADER ^ FlyPrime.TAKE_MANY)
    dos.writeLong(matchLimit)
    retrieveMany(template)
  }

  private[this] def retrieveMany[T <: AnyRef](template: T): Iterable[T] = {
    val entryCount = remoter.sendOperation(bos.toByteArray).toInt
    for (i ← 0 until entryCount; o ← typeChain.readObject(theClass(template))) yield o
  }

  def notifyWrite(template: AnyRef, leaseTime: Long, actor: Actor, returnsEntry: Boolean): Boolean =
    notify(template, leaseTime, actor, if (returnsEntry) FlyPrime.NOTIFY_WRITE_OBJECT else FlyPrime.NOTIFY_WRITE)

  def notifyTake(template: AnyRef, leaseTime: Long, actor: Actor, returnsEntry: Boolean): Boolean =
    notify(template, leaseTime, actor, if (returnsEntry) FlyPrime.NOTIFY_TAKE_OBJECT else FlyPrime.NOTIFY_TAKE)

  private[this] def notify(template: AnyRef, leaseTime: Long, actor: Actor, op: Int) = remoterOp {
    bos.reset()
    dos.writeInt(FlyPrime.FLY_HEADER ^ op)
    dos.writeInt(typeChain.getChannel(template))
    typeChain.writeObject(dos, template)
    dos.writeLong(leaseTime)
    val notifyToken = remoter.sendOperation(bos.toByteArray)
    remoter.addNotifyDetails(notifyToken, actor, theClass(template))
    true
  }

  private[this] def remoterOp[X](op: ⇒ X): X = synchronized {
    try {
      op
    } catch {
      case e: Exception ⇒ throw new FlyAccessException(e)
    } finally {
      remoter.setMessageComplete()
    }
  }
}
