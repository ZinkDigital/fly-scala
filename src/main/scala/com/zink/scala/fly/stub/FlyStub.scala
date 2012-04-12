package com.zink.scala.fly.stub

import java.lang.Thread
import java.net.InetAddress

import scala.actors.Actor.actor
import scala.actors.Actor.loop
import scala.actors.Actor.react
import scala.actors.Actor

import com.zink.scala.fly.FlyAccessException.wrapExceptionIfThrown
import com.zink.scala.fly.kit.Wait.waitUpTo
import com.zink.scala.fly.kit.Logging
import com.zink.scala.fly.FieldCodec
import com.zink.scala.fly.Fly
import com.zink.scala.fly.FlyPrime
import com.zink.scala.fly.Notifiable
import com.zink.scala.fly.NotifyHandler
import com.zink.scala.fly.NotifyHandlerReturningEntry

/**
 *  The core implementation of Fly.
 *  FlyStub delegates to <code>MethodCodec</code> for all operations
 *  except <code>writeMany</code> and <code>retrieve</code>.
 */
class FlyStub(codec: MethodCodec) extends Fly with Logging {

  def this(address: InetAddress, fieldCodec: FieldCodec) = this(new MethodCodec(address, fieldCodec))
  def this(hostname: String, fieldCodec: FieldCodec) = this(new MethodCodec(hostname, fieldCodec))

  private[this] val sampleTime = 100L

  def read[T <: AnyRef](template: T, timeout: Long): Option[T] = retrieve(template, timeout, codec.read[T] _)

  def take[T <: AnyRef](template: T, timeout: Long): Option[T] = retrieve(template, timeout, codec.take[T] _)

  /**
   * Retrieve objects from the space, trying repeatedly until the timeout is reached or the object is found.
   * @param template the template to use
   * @param timeout the timeout
   * @param method the MethodCodec method to use to retrieve the object. Either take or read
   */
  private[this] def retrieve[T <: AnyRef](template: T, timeout: Long, method: (T, Long) ⇒ Option[T]): Option[T] = {
    val nyquist = math.min(timeout + 1L >> 1, sampleTime)
    waitUpTo(timeout, nyquist).untilSome(method(template, 0L))
  }

  def write(entry: AnyRef, lease: Long): Long = codec.write(entry, lease)

  def snapshot(template: AnyRef): AnyRef = codec.snapshot(template)

  /**
   *  Write many entries into the space.
   *  This implementation iterates over <code>entries</code> writing each
   *  to the space in turn.
   * @return the lease of the last entry written
   */
  def writeMany(entries: Iterable[AnyRef], lease: Long): Long = (0L /: entries) { (previousLease, nextEntry) ⇒ codec.write(nextEntry, lease) }

  def readMany[T <: AnyRef](template: T, matchLimit: Long): Iterable[T] = codec.readMany(template, matchLimit, 0L)

  def readMany[T <: AnyRef](template: T, matchLimit: Long, ignoreInitialMatches: Long): Iterable[T] = codec.readMany(template, matchLimit, ignoreInitialMatches)

  def takeMany[T <: AnyRef](template: T, matchLimit: Long): Iterable[T] = codec.takeMany(template, matchLimit)

  @deprecated("This will be removed in Fly 2", "1.x")
  def notify(template: AnyRef, leaseTime: Long, actor: Actor): Boolean = notifyWrite(template, leaseTime, actor)

  @deprecated("This will be removed in Fly 2", "1.x")
  def notify(template: AnyRef, handler: Notifiable, leaseTime: Long): Boolean = notifyWrite(template, handler, leaseTime)

  @deprecated("This will be removed in Fly 2", "1.x")
  def notify(template: AnyRef, leaseTime: Long)(block: ⇒ Unit): Boolean = notifyWrite(template, leaseTime)(block)

  def notifyWrite(template: AnyRef, handler: Notifiable, leaseTime: Long): Boolean = handler match {
    case h: NotifyHandler ⇒ notifyWrite(template, leaseTime) { h.templateMatched() }
    case h: NotifyHandlerReturningEntry ⇒ notifyWrite(template, leaseTime, actor {
      loop {
        react {
          case Some(matched: AnyRef) ⇒ h templateMatched matched
        }
      }
    }, true)
  }

  def notifyWrite(template: AnyRef, leaseTime: Long)(block: ⇒ Unit): Boolean = notifyWrite(template, leaseTime, actor {
    loop {
      react {
        case FlyPrime.ACTOR_MESSAGE ⇒ block
      }
    }
  }, false)

  def notifyWrite(template: AnyRef, leaseTime: Long, actor: Actor): Boolean = notifyWrite(template, leaseTime, actor, false)

  private[this] def notifyWrite(template: AnyRef, leaseTime: Long, actor: Actor, returnsValue: Boolean): Boolean = codec.notifyWrite(template, leaseTime, actor, returnsValue)

  def notifyTake(template: AnyRef, leaseTime: Long, actor: Actor): Boolean = unsupported

  def notifyTake(template: AnyRef, handler: Notifiable, leaseTime: Long): Boolean = unsupported

  def notifyTake(template: AnyRef, leaseTime: Long)(block: ⇒ Unit): Boolean = unsupported

  private[this] def unsupported = throw new UnsupportedOperationException("Not supported in this version")

  private[this] def sleep(x: Long) = wrapExceptionIfThrown(Thread.sleep(x))
}
