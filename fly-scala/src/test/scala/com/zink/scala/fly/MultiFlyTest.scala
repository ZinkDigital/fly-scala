package com.zink.scala.fly

import com.zink.scala.fly.kit.FlyFactory
import scala.collection.mutable.ArrayBuffer
import org.specs2.mutable._
import org.specs2.specification._
import org.specs2.execute._

class MultiFlyTest extends SpecificationWithJUnit {

  val instance = FlyFactory()

  "WriteMany" in {
    val TEST_CODE = "MultiFly1"
    val numEntries = 100

    val entries = new ArrayBuffer[TestEntry]

    for (i <- 0 until numEntries) {
      entries += new TestEntry(TEST_CODE, i, "payload")
    }

    instance.writeMany(entries, 10 * 1000)

    val template = new TestEntry(name = TEST_CODE)

    var countBack = 0
    // count the entries back out with a standard take
    while (instance.take(template, 0L) != None) {
      countBack += 1
    }
    numEntries mustEqual countBack
  }

  "ReadMany" in {
    val TEST_CODE = "MultiFly2"

    val numEntries = 35
    for (i <- 0 until numEntries) {
      instance.write(new TestEntry(TEST_CODE, i, "payload"), 1000)
    }

    val template = new TestEntry(name = TEST_CODE)
    val numToRead = 10

    "read limited number of entries" in {
      var entries = instance.readMany(template, numToRead)

      // Check the number of entries
      entries.size mustEqual numToRead
      entries.toList.head.reference mustEqual BigInt(0)
    }

    "ignore specified number of entries" in {
      // now test the ignore parameter of method
      val ignore = 10
      val entries = instance.readMany(template, numToRead, ignore)

      entries.size mustEqual numToRead
      entries.toList.head.reference mustEqual BigInt(ignore)
    }

    "read too many should return the available instances" in {
      // try to read more than the list
      val ignore = 25
      val entries = instance.readMany(template, numToRead, ignore)

      entries.size mustEqual numEntries - ignore
      entries.toList.head.reference mustEqual BigInt(ignore)
    }
  }

  "TakeMany" in {
    val TEST_CODE = "MultiFly3"

    val numEntries = 25
    for (i <- 0 until numEntries) {
      instance.write(new TestEntry(TEST_CODE, i, "payload"), 1000)
    }

    val template = new TestEntry(name = TEST_CODE)

    val numToTake = 10
    var entries = instance.takeMany(template, numToTake)

    entries.size mustEqual numToTake
    entries.toList.head.reference mustEqual BigInt(0)

    entries = instance.takeMany(template, numToTake)

    entries.size mustEqual numToTake
    entries.toList.head.reference mustEqual BigInt(numToTake)

    // try tl read more than the list
    entries = instance.takeMany(template, numToTake)

    entries.size mustEqual numEntries - numToTake * 2
    entries.toList.head.reference mustEqual BigInt(numToTake * 2)
  }
}
