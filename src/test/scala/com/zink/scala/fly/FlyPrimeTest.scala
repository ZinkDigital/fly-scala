package com.zink.scala.fly

import org.scalatest._

class FlyPrimeTest extends FreeSpec with MustMatchers {

  val HOST = "localhost"
  val fly: ScalaFly = ScalaFly.makeFly().right.get
  val template = new TestEntry("FlyPrime 1")

  "WriteTake" - {
    val entry = MakeTestEntry("FlyPrime 1", BigInt(1), 200)

    fly.write(entry, 1000L)
    fly.take(template, 0L) mustBe Some(entry)

    // now try and take it again to prove that
    // there is nothing there
    fly.take(template, 0L) mustBe None
  }

  "WriteRead" - {
    val entry = MakeTestEntry("FlyPrime 1", BigInt(2), 200)

    fly.write(entry, 1000L)
    fly.read(template, 0L) mustBe Some(entry)

    // to test the read left a copy do a take
    // make sure it is the same again
    fly.take(template, 0L) mustBe Some(entry)
  }

  "SnapShot" - {
    val entry = MakeTestEntry("FlyPrime 1", BigInt(3), 200)
    fly.write(entry, 1000L)
    val snapshot = fly.snapshot(template)
    fly.take(snapshot, 0L) mustBe Some(entry)
  }

  "LargeObject" - {
    val entry = MakeTestEntry("FlyPrime 1", BigInt(4), 5000)
    fly.write(entry, 1000L)
    fly.take(template, 0L) mustBe Some(entry)
  }

  "WaitingTake" - {
    val entry = MakeTestEntry("FlyPrime 1", BigInt(5), 512)
    val TAKE_TIME = 576L

    fly.take(template, TAKE_TIME) mustBe None
  }
}
