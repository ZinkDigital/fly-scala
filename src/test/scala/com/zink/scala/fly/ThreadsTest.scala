package com.zink.scala.fly

import com.zink.scala.fly.example.{WriteRead, WriteTake}
import org.specs2.mutable._

class ThreadsTest extends Specification {
  
   "test Threads" in {
       
    val thrd1 = new Reader()
    val thrd2 = new Taker()

    thrd1.start()
    Thread.sleep(10)
    thrd2.start()
    thrd1.join()
    thrd2.join()
    "make specs happy" must_!= null
  }
    
   class Taker extends Thread {
       override def run() = {
           WriteTake.main(new Array[String](0) )  
       }
   }

   class Reader extends Thread {
       override def run() = {
           WriteRead.main(new Array[String](0))  
       }
   }
}
