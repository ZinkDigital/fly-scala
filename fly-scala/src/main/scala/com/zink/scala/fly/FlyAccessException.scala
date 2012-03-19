package com.zink.scala.fly

import java.util.logging._

object FlyAccessException {
  def wrapExceptionIfThrown[T](block: ⇒ T) =
    try {
      block
    } catch {
      case ex: Exception ⇒ throw new FlyAccessException(ex)
    }

  def wrappingExceptionsIn_:[T](block: ⇒ T) = wrapExceptionIfThrown(block)

  def logIfThrown[T](logger: Logger)(block: ⇒ T) =
    try {
      block
    } catch {
      case ex ⇒ logger.log(Level.SEVERE, null, ex)
    }
}

class FlyAccessException(message: String, cause: Throwable) extends RuntimeException(message, cause) {
  def this(cause: Throwable) = this("Error accessing Fly caused by :", cause)
  def this(narrative: String) = this(narrative, null)
}
