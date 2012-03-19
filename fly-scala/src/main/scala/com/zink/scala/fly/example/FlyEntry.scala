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

object FlyEntry {
  def apply(name:String, reference:BigInt, payloadSize:Int):FlyEntry = {
    val f = new FlyEntry(name, reference)
    f.setPayloadOfSize(payloadSize)
    f
  }
}

class FlyEntry(var name:String = null, var reference:BigInt = null, var payload:String = null) {
	
  /**
   * This constructor is needed so that the class can be instantiated by reflection
   */
  def this() = this(name = null, reference = null, payload = null)
  
  override def toString: String = {
    val sb = new StringBuilder(128)
    sb.append("Name :")
    sb.append(name)
    sb.append("\n")
    sb.append("Reference :")
    sb.append(reference.toString())
    sb.append("\n")
    sb.append("Payload size :")
    sb.append(payload.length())
    sb.append("\n")
    sb.toString()
  }


  def setPayloadOfSize(payloadSize: Int) {
    val sb = new StringBuilder(payloadSize)
    for (i <- 0 until payloadSize) {
      sb.append('z')
    }
    payload = sb.toString()
  }
}
