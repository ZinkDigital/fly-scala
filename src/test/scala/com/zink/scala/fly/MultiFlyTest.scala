package com.zink.scala.fly

import scala.collection.mutable.ArrayBuffer
import org.specs2.mutable._
import org.specs2.specification._
import org.specs2.execute._

class MultiFlyTest extends SpecificationWithJUnit {

  val fly: ScalaFly = ScalaFly.makeFly().get

  "WriteMany" in {
    val TEST_CODE = "MultiFly1"
    val numEntries = 100

    val entries = new ArrayBuffer[TestEntry]


    for (i <- 0 until numEntries) {

      entries += new TestEntry(TEST_CODE, i, "payload")
    }

    fly.writeMany(entries, 10 * 1000)

    val template = new TestEntry(name = TEST_CODE)

    var countBack = 0
    // count the entries back out with a standard take
    while (fly.take(template, 0L) != None) {
      countBack += 1
    }
    numEntries mustEqual countBack
  }

  "ReadMany" in {
    val TEST_CODE = "MultiFly2"

    val numEntries = 35
    for (i ← 0 until numEntries) {
      fly.write(new TestEntry(TEST_CODE, i, "payload"), 1000)
    }

    val template = new TestEntry(name = TEST_CODE)
    val numToRead = 10

    "read limited number of entries" in {
      var entries = fly.readMany(template, numToRead)

      // Check the number of entries
      entries.size mustEqual numToRead
      entries.toList.head.reference mustEqual BigInt(0)
    }

  }

  "TakeMany" in {
    val TEST_CODE = "MultiFly3"

    val numEntries = 25
    for (i ← 0 until numEntries) {
      fly.write(new TestEntry(TEST_CODE, i, "payload"), 1000)
    }

    val template = new TestEntry(name = TEST_CODE)

    val numToTake = 10
    var entries = fly.takeMany(template, numToTake)

    entries.size mustEqual numToTake
    entries.toList.head.reference mustEqual BigInt(0)

    entries = fly.takeMany(template, numToTake)

    entries.size mustEqual numToTake
    entries.toList.head.reference mustEqual BigInt(numToTake)

    // try tl read more than the list
    entries = fly.takeMany(template, numToTake)

    entries.size mustEqual numEntries - numToTake * 2
    entries.toList.head.reference mustEqual BigInt(numToTake * 2)
  }
}
