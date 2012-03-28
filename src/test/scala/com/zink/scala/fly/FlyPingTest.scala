package com.zink.scala.fly

import com.zink.scala.fly.kit.FlyPinger
import com.zink.scala.fly.stub.Remoter
import java.net.InetAddress
import java.nio.ByteBuffer
import org.specs2.mutable._
import org.specs2.specification._
import org.specs2.execute._
import javax.management.remote.rmi._RMIConnection_Stub


class FlyPingTest extends Specification {
	
   "Ping With Remoter" in {
        // Use the remoter to send the ping reques 
        val remoter = new Remoter("localhost", 4396)
        val bb = ByteBuffer.allocate(4)
          
        // just the header is a valid ping message 
        bb.putInt(FlyPrime.FLY_HEADER)
        
        val reply = remoter.sendOperation(bb.array())
        println("Number of tags [" + reply + "]")
        
        reply > 0  must beTrue
        // get the tags from the reply

        val tags = for (i <- 0 until reply.toInt;
            tag = remoter.readString()) yield {
            println("Tag " + (i+1) + " [" + tag + "]")
            tag
        }
        tags.size must_== reply.toInt
        tags.headOption must_== Some("FlySpace")
    }

	
    "Ping With Pinger" in {
        new FlyPinger().ping(InetAddress.getByName("localhost")).map(_(0) mustEqual "FlySpace") must_!= None
    }
}
