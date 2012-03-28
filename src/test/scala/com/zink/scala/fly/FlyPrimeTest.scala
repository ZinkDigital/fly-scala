package com.zink.scala.fly

import com.zink.scala.fly.kit.FlyFactory

import org.specs2.mutable._
import org.specs2.specification._
import org.specs2.execute._

class FlyPrimeTest extends Specification {
	
  val HOST = "localhost"
  val fly = FlyFactory()
  val template = new TestEntry("FlyPrime 1")

  override def is = args(sequential = true) ^ super.is 

  "WriteTake" in {
    val entry = MakeTestEntry("FlyPrime 1", BigInt(1), 200)

    fly.write(entry, 1000L)
    fly.take(template, 0L) must beSome(entry)

    // now try and takem it again to prove that
    // there is nothing there
    fly.take(template, 0L) must beNone
  }

  "WriteRead" in {
    val entry = MakeTestEntry("FlyPrime 1", BigInt(2), 200)

    fly.write(entry, 1000L)
    fly.read(template, 0L) must beSome(entry)

    // to test the read left a copy do a take
    // make sure it is the same again
    fly.take(template, 0L) must beSome(entry)
  }

  "SnapShot" in {
    val entry = MakeTestEntry("FlyPrime 1", BigInt(3), 200)
    fly.write(entry, 1000L)
    val snapshot = fly.snapshot(template)
    fly.take(snapshot, 0L) must beSome(entry)
  }


  "LargeObject" in {
      val entry = MakeTestEntry("FlyPrime 1", BigInt(4), 5000)
      fly.write(entry, 1000L)
      fly.take(template, 0L) must beSome(entry)
  }


  "WaitingTake" in {
    val entry = MakeTestEntry("FlyPrime 1", BigInt(5), 512)
    val TAKE_TIME = 576

    fly.take(template, TAKE_TIME) must beNone
  }
}