package com.zink.scala.fly.kit

import com.zink.scala.fly.FlyPrime
import com.zink.scala.fly.stub.StringCodec._
import java.io.DataInputStream
import java.net.{InetAddress, InetSocketAddress, Socket}
import java.util.concurrent.TimeoutException
import scala.util.control.Exception._
import Wait._
import java.io.DataOutputStream
	  
object FlyPinger {
  val DEFAULT_SOCKET_TIMEOUT: Int = 50 //ms
}

class FlyPinger(socketTimeout: Int = FlyPinger.DEFAULT_SOCKET_TIMEOUT) extends Logging {


  /**
   * Send a ping message to a Fly Space running on the given Address
   * prefer the method that takes an InetAddress. Name to address 
   * conversion can be very slow on some platforms
   * @param host
   * @return String [] of tags or null array for failed ping.
   */   
  def ping(host:String):Option[Array[String]] = ping(InetAddress.getByName(host))

  /**
   *  Send a ping message to a Fly Space running on the given InetAddress and
   *  port.
   * @param addr
   * @return An Option of an array of tags or None for failed ping.
   */
  def ping(addr: InetAddress): Option[Array[String]] = {
    val sock = new Socket()
    sock.setKeepAlive(true)
    
    def shutdown {
      sock.setKeepAlive(false)
      sock.shutdownInput()
      sock.shutdownOutput()
    }
    
    allCatch.andFinally(sock.close _).opt {
      sock.bind(null)
      sock.connect(new InetSocketAddress(addr, FlyPrime.FLY_PORT), socketTimeout)
      val dis = new DataInputStream(sock.getInputStream)
      val dos = new DataOutputStream(sock.getOutputStream())
      dos.writeInt(FlyPrime.FLY_HEADER);

      if (!waitUpTo(socketTimeout, 1).forCondition(dis.available() != 0)) throw new TimeoutException("No reply from fly server")
      val tags = (for (i <- 0 until dis.readLong().toInt) yield readString(dis)).toArray
      shutdown
      tags
    }
  }
}
