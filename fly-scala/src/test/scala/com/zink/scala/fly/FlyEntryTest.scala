package com.zink.scala.fly


import com.zink.scala.fly.kit.FlyFactory

import org.specs2.mutable._

class FlyEntryTest extends SpecificationWithJUnit {
  
  val fly = FlyFactory(host = "localhost")
  
  "Empty Entry" in {
    // set up the empty entry
    val leaseTime = 60 * 1000L
    val entry = new EmptyEntry()
       
    val lease = fly.write(entry, leaseTime)
    lease mustEqual leaseTime
       
    // then read it back 
    fly.take(entry, 0) must beSome[EmptyEntry]
  }
  
  
  "Exotic Entry" in {
        
    val FIRST_NAME = "Dan"
    val LAST_NAME = "Tucker"
        
    val leaseTime = 500L
    val entry = new ExoticEntry()
    entry.name1 = FIRST_NAME
    entry.setName2(LAST_NAME)
    fly.write(entry, leaseTime)
       
    // then read it back 
    fly.read(entry, 0) must beSome(entry)
  }
}
