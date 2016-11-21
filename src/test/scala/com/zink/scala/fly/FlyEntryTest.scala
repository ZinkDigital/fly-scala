package com.zink.scala.fly

import org.scalatest._

class FlyEntryTest extends FlatSpec with MustMatchers {

  val fly: ScalaFly = ScalaFly.makeFly().right.get

  "Empty Entry" must "be read" in {
    // set up the empty entry
    val leaseTime: Long =  100L
    val entry = new EmptyEntry()

    val lease = fly.write(entry, leaseTime)
    lease mustEqual leaseTime

    // then read it back
    fly.take(entry, 0) mustBe a [Some[_]]
  }

  "Entry with Long.MaxValue timeout" must "be read" in {
    // set up the empty entry
    val leaseTime = Long.MaxValue
    val entry = new EmptyEntry()

    val lease = fly.write(entry, leaseTime)
    lease mustEqual leaseTime

    // then read it back
    fly.take(entry, 0) mustBe a [Some[_]]
  }

  "Exotic Entry" must "be read" in {

    val FIRST_NAME = "Dan"
    val LAST_NAME = "Tucker"

    val leaseTime = 500L
    val entry = new ExoticEntry()
    entry.name1 = FIRST_NAME
    entry.setName2(LAST_NAME)
    fly.write(entry, leaseTime)

    // then read it back
    fly.read(entry, 0) mustBe Some(entry)
  }
}
