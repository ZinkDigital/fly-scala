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

import com.zink.scala.fly.stats.StatsDecoder
import com.zink.scala.fly.stats.StatsPrinter

import java.net.Socket

/**
 *  Log the server's stats periodically
 */
object Stats extends App {
  try {

    val HOST = "localhost"
    val DEFAULT_PORT = 4396

    println("Stats started.")

    val sleep = IntegerArgument(args, 1000)

    val socket = new Socket(HOST, DEFAULT_PORT)
    val statser = new StatsDecoder()

    while (true) {
      statser.stats(socket) match {
        case Nil ⇒
        case beans ⇒
          StatsPrinter.writeHeader(beans)
          StatsPrinter.writeStats(beans)
      }
      Thread.sleep(sleep)
    }
  } catch {
    case ex: Exception ⇒ println("Stats connection broken :" + ex.getMessage)
  }
}
