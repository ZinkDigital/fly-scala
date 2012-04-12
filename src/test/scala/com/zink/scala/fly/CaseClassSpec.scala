package com.zink.scala.fly

import com.zink.scala.fly.kit.FlyFactory

import org.specs2.mutable._

case class SpecialCase(var x: Int, var y: String)

case class ReallySpecialCase(var z: Int, var s: SpecialCase)

class CaseClassSpec extends SpecificationWithJUnit {

  val fly = FlyFactory(host = "localhost")
  
  "Fly" should {
    "put and take simple case classes" in {
      val special = SpecialCase(3, "hi")
      fly.write(special, 1000L)
      fly.take(special, 0) must beSome(special)
    }
    
    "put and take nested case classes" in {
      val special = SpecialCase(3, "hi")
      val nothingSpecial = ReallySpecialCase(10, special)
      
      fly.write(nothingSpecial, 1000L)
      fly.take(nothingSpecial, 0) must beSome(nothingSpecial)
    }
  }
}