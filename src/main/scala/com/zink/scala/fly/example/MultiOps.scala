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

import com.zink.scala.fly._
import com.zink.scala.fly.kit.FlyFactory

object MultiOps extends App {
  val LEASE_TIME = 100 * 1000

  val space = FlyFactory()

  // Set up a payload size and the number of objects to write and
  // read - tune these for your testing/deployment purposes
  val payloadSize = 20
  val entryCount = IntegerArgument(args, 1000)

  // set up a list of entries that we can use to write to the space
  val entries = 0 until entryCount map (i ⇒ FlyEntry("MultiFly", BigInt(i), payloadSize))

  // set up a template to match the entries
  val template = new FlyEntry("MultiFly");
  template.reference = null; // match any value in this template
  template.payload = null // ditto

  // do a single write read and and take and show the result
  Timing("Doing " + entryCount + " writes reads and takes with single methods ...") {
    writeReadAndTakeSingle(space, template, entries)
  }

  // now do the same thing using multi
  Timing("Doing " + entryCount + " writes reads and takes with multi methods ...") {
    writeReadAndTakeMulti(space, template, entries)
  }

  private def writeReadAndTakeSingle(space: MultiFly, template: FlyEntry, entries: Seq[FlyEntry]) {

    // write the objects
    for (entry ← entries) {
      space.write(entry, LEASE_TIME)
    }
    // read the objects
    for (entry ← entries) {
      space.read(template, 0)
    }
    // take the objects
    for (entry ← entries) {
      space.take(template, 0)
    }
  }

  private def writeReadAndTakeMulti(space: MultiFly, template: FlyEntry, entries: Seq[FlyEntry]) {

    // write the objects
    val lease = space.writeMany(entries, LEASE_TIME)
    println("done writing with lease " + lease)

    // read the objects
    val reads = space.readMany(template, entries.size)
    println("done reading " + reads.size + " objects")

    // take the objects
    val takes = space.takeMany(template, entries.size)
    println("done taking " + takes.size + " objects")
  }
}