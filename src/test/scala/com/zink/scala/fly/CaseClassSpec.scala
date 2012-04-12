package com.zink.scala.fly

import com.zink.scala.fly.kit.FlyFactory

import org.specs2.mutable._

case class SpecialCase(x: BigInt, y: String)

case class ReallySpecialCase(z: BigInt, s: SpecialCase)

class CaseClassSpec extends SpecificationWithJUnit {

  val fly = FlyFactory(host = "localhost")
  
  "Fly" should {
    "put and take simple case classes" in {
      val special = SpecialCase(3, "hi")
      fly.write(special, 1000L)
      
      fly.take(SpecialCase(3, "Nope"), 0) must beNone
      fly.take(SpecialCase(null, null), 0) must beSome(special)
    }
    
    "put and take nested case classes" in {
      val special = SpecialCase(3, "hi")
      val nothingSpecial = ReallySpecialCase(10, special)
      
      fly.write(nothingSpecial, 1000L)
      fly.take(ReallySpecialCase(null, null), 0) must beSome(nothingSpecial)
    }
  }
}