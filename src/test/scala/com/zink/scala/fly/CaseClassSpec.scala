package com.zink.scala.fly

import org.scalatest._

case class SpecialCase(x: BigInt, y: String)

case class ReallySpecialCase(z: BigInt, s: SpecialCase)

class CaseClassSpec extends FlatSpec with MustMatchers {

  val fly: ScalaFly = ScalaFly.makeFly().right.get

  "Fly" must "put and take simple case classes" in {
    val special = SpecialCase(3, "hi")
    fly.write(special, 1000L)

    fly.take(SpecialCase(3, "Nope"), 0) mustBe None
    fly.take(SpecialCase(null, null), 0) mustBe Some(special)
  }

  it should "put and take nested case classes" in {
    val special = SpecialCase(3, "hi")
    val nothingSpecial = ReallySpecialCase(10, special)

    fly.write(nothingSpecial, 1000L)
    fly.take(ReallySpecialCase(null, null), 0) mustBe Some(nothingSpecial)
  }
}
