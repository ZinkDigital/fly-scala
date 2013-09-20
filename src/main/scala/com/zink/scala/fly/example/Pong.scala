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

import com.zink.scala.fly.ScalaFly
import com.zink.fly.FlyPrime

/**
 *  Pong is a player in a game of ping pong, bouncing a Ball instance via the fly space.
 */
object Pong extends App {

  println("Pong started.")

  val shots = IntegerArgument(args, 100)

  println("Ready to play " + shots + " shots")

  // NEVER do a get on an Option, except in a demo
  val fly: ScalaFly = ScalaFly.makeFly().get

  val template = new Ball("Ping")

  fly.take(template, 0L) match {

    case None => {

      println("No ball in play")
      serveBall(fly)
      println("Served Ball - Please start a Ping")
    }

    case Some(gameBall) => {

      System.out.println("Received ball - game on!")
      returnBall(fly, gameBall)
    }
  }

  var myShots = 1
  while (myShots < shots) {
    fly.take(template, 0L) match {

      case None => {
        Thread.sleep(10)
      }
      case Some(ball) => {

        returnBall(fly, ball)
        myShots += 1
        if (myShots % 10 == 0) print(".")
      }
    }
  }
  println("\nPlayed all my " + myShots + " shots")

  private def serveBall(fly: ScalaFly) {
    val gameBall = new Ball("Pong", BigInt(1))
    fly.write(gameBall, 60 * 1000L)
  }

  private def returnBall(fly: ScalaFly, ball: Ball) {
    val hitCount = ball.batted.intValue()
    val returned = new Ball("Pong", ball.batted + 1)
    fly.write(returned, 1 * 1000L)
  }
}
