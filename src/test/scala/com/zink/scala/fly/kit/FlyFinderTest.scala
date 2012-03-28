package com.zink.scala.fly.kit

import com.zink.scala.fly._

import org.specs2.mutable._
import org.specs2.specification._
import org.specs2.execute._

class FlyFinderTest extends Specification {

    "No Tags" in {
        new FlyFinder().find must beSome[Fly]
    }

    
    "Tags Match" in {
      new FlyFinder().find(Array { "FlySpace" }) must beSome[Fly]
    }
    
    "Tags No Match" in {
        new FlyFinder().find(Array{ "NadaFlySpace" } ) must beNone
    }
   
}
