package com.zink.scala.fly

object MakeTestEntry {
	
	def apply(name:String, reference:BigInt, payloadSize:Int) = new TestEntry(name, reference, payloadOfSize(payloadSize))
	
	def payloadOfSize(payloadSize:Int):String = {
        val sb = new StringBuilder(payloadSize)
        for (i <- 0 until payloadSize) sb.append('z')
        sb.toString()
    }
}

class TestEntry(var name:String = null, var reference:BigInt = null, var payload:String = null) {

	override def equals(that:Any) = that match {
      case other:TestEntry => (other canEqual this) && name == other.name && reference == other.reference && payload == other.payload
      case _ => false
    }

  private def canEqual(other:Any):Boolean = other.isInstanceOf[TestEntry]

    override def hashCode():Int = {
        var hash = 7
        hash = 71 * hash + (if (this.name != null) this.name.hashCode() else 0)
        hash = 71 * hash + (if (this.reference != null) this.reference.hashCode() else 0)
        hash = 71 * hash + (if (this.payload != null) this.payload.hashCode() else 0)
        hash
    }

	override def toString:String = {
        val sb = new StringBuilder(128)
        sb.append("Name :")
        sb.append(name)
        sb.append("\n")
        sb.append("Reference :")
        sb.append(reference.toString())
        sb.append("\n")
        sb.append("Payload: ")
        sb.append(payload)
        sb.append("\n")
        sb.toString()
    }
}
