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

import com.zink.scala.fly.{ Fly, FlyPrime }
import com.zink.scala.fly.kit.FlyFinder
import scala.actors.Actor
import scala.actors.Actor._

/**
 *  This example creates several actors that pass a Ball between them.
 *  Each actor first registers itself to be notified when a Ball appears
 *  in the space with a name equal to the actor's name.
 *  When an actor receives a notification, it takes the ball from the
 *  space and puts it back with ball.name set to the next actor.
 *  The last actor passes the ball back to the first actor.
 */
object RoundRobinActors extends App {
  val fly: Fly = findFly()

  // create actors, with their name and the name of the actor to pass the ball to
  val mickey = createActor("mickey", "donald")
  createActor("donald", "betty")
  createActor("betty", "tom")
  createActor("tom", "gerry")
  createActor("gerry", "mickey")

  // tell mickey to start the ball rolling
  mickey ! "Go"

  def createActor(name: String, next: String): Actor = {
    // we are interested in balls passed to 'name'
    val template = new Ball(name)

    val anActor = actor {
      loop {
        react {
          case FlyPrime.ACTOR_MESSAGE => fly.take(template, 0L).map(passBall(_, name, next))

          // we've been asked to start the game so put a ball in the space
          case "Go"                   => fly.write(new Ball(name, 1), 10 * 1000L)
        }
      }
    }
    fly.notifyWrite(template, -1L, anActor)
    anActor
  }

  def passBall(ball: Ball, from: String, next: String) {
    val count = ball.batted.intValue()
    println(from + " is passing the ball to " + next + ", the pass count is " + ball.batted)
    Thread.sleep(500)
    fly.write(new Ball(next, ball.batted + 1), 1 * 1000L)
  }

  def findFly(): Fly = {
    FlyFinder.find() match {
      case None => {
        System.err.println("Failed to find a Fly Server running on the local network")
        System.exit(1)
        null
      }
      case Some(x) => x
    }
  }
}
