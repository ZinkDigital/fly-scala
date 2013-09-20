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

object WriteRead extends App {

  val iterations = IntegerArgument(args, 1000)

  // NEVER do a get on an Either, except in a demo
  val fly: ScalaFly = ScalaFly.makeFly().right.get

  // set up an object to write to the space
  val obj = new FlyEntry(name = "Fly 1", reference = BigInt(1))
  obj.setPayloadOfSize(100)

  // set up a template to match the above object 
  val template = new FlyEntry(name = "Fly 1")
  template.reference = null // match any value in this template
  template.payload = null // ditto

  Timing("Processing " + iterations + " writes and reads", iterations) {
    fly.write(obj, 1000)
    fly.read(template, 0L)
  }
}