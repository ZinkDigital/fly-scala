package com.zink.scala.fly.kit

import com.zink.scala.fly.{FlyAccessException, FlyPrime}
import com.zink.scala.fly.FlyAccessException._
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.MulticastSocket

class MulticastRequestor {
   private[this] val bytesAddress = Array(232.toByte, 43.toByte, 96.toByte, 232.toByte)
   
   private[this] val ttl = 1
   private[this] val sendBytes = "FLY\n".getBytes
   
   private[this] val multicastAddr = InetAddress.getByAddress(bytesAddress) wrappingExceptionsIn_: FlyAccessException
   private[this] val packet = new DatagramPacket(sendBytes, sendBytes.length, multicastAddr, FlyPrime.FLY_PORT) wrappingExceptionsIn_: FlyAccessException

   def sendRequest() {
     wrapExceptionIfThrown {
       val sock = new MulticastSocket()
       sock.setTimeToLive(ttl)
       sock.send(packet)
       sock.close()
     }
   }
}
