package com.zink.scala.fly

import com.zink.scala.fly.example.{WriteRead, WriteTake}

import org.scalatest._

class ThreadsTest extends FreeSpec with MustMatchers {

   "test Threads" in {

    val thrd1 = new Reader()
    val thrd2 = new Taker()

    thrd1.start()
    Thread.sleep(10)
    thrd2.start()
    thrd1.join()
    thrd2.join()
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
