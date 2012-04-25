package com.zink.scala.fly.kit

import scala.annotation.tailrec

class Wait(millis: Long, sleep: Long = 0) {

  def forCondition(f: ⇒ Boolean): Boolean = untilSome(if (f) Some(true) else None).isDefined

  def untilSome[T](f: ⇒ Option[T]): Option[T] = {
    val end_? = System.currentTimeMillis() + millis
    val end = if (end_? > 0) end_? else Long.MaxValue
    
    def done(last: Option[T]) = last.isDefined || System.currentTimeMillis() >= end

    @tailrec
    def loop(last: Option[T]): Option[T] = if (done(last)) last else { Thread.sleep(sleep); loop(f) }

    loop(f)
  }
}

object Wait {
  def waitUpTo(millis: Long, sleep: Long = 0) = new Wait(millis, sleep)
}