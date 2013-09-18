/*


Permission to use, copy, modify, and distribute this software for any 
purpose without fee is hereby granted, provided that this entire notice 
is included in all copies of any software which is or includes a copy 
or modification of this software and in all copies of the supporting 
documentation for such software.

THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/
package com.zink.scala.fly.example

import scala.actors.Actor._
import com.zink.fly.Fly
import com.zink.fly.kit.FlyFactory
import com.zink.scala.fly.ScalaFly
import com.zink.scala.fly.ScalaFly._
import com.zink.fly.kit.FlyFinder

object Notification extends App {
  val LEASE = 1 * 1000L;

  val fly: ScalaFly = ScalaFly.makeFly() match {
    case None ⇒ {
      System.err.println("Failed to find a Fly Server running on the local network")
      System.exit(1)
      null
    }
    case Some(x) ⇒ x
  }

  println("Setting up notify handlers")
  setUpWriteNotify(fly)

  // wont match so nothing printed from the callback handler
  writeNonMatchingEntry(fly)

  // write a matching entry so handlers 'fire'
  writeMatchingEntry(fly)

  // let the callbacks threads write to the sys out
  Thread.`yield`()

  // wait just over a second for the lease to expire
  println("Waiting for Notify handler's lease to expire")
  Thread.sleep(LEASE + 100)

  // The lease has expired so this wont fire the callback
  writeMatchingEntry(fly)

  println("End.")
  System.exit(0)

  private def setUpWriteNotify(fly: ScalaFly) {
    val template = new FlyEntry()

    template.name = "Example NotiFly Entry" // match this string
    template.reference = null // match anything
    template.payload = null // match anything

    fly.notifyWrite(template, new CallbackHandler(), LEASE)
    fly.notifyWrite(template, new CallbackWithReturningEntry(), LEASE)

    // now with block rather than a handler
    fly.notifyWrite(template, LEASE) {
      println("Block Template matched!")
    }

    // now with an actor
    val myActor = actor {
      loop {
        react {
          case ACTOR_MESSAGE ⇒
            println("Actor received a message!")
        }
      }
    }

    fly.notifyWrite(template, LEASE, myActor)
  }

  private def writeNonMatchingEntry(fly: ScalaFly) {
    val entry = new FlyEntry(name = "Not a matching entry", reference = BigInt(7), payload = new String("Seven"))
    fly.write(entry, LEASE)
  }

  private def writeMatchingEntry(fly: ScalaFly) {
    val entry = new FlyEntry(name = "Example NotiFly Entry", reference = BigInt(11), payload = new String("Eleven"))
    fly.write(entry, LEASE)
  }

}