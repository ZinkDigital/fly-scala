package com.zink.scala.fly

object ExoticEntry {
  
    val name3 = "publicStatic"
    
    private val name4 = "privateStatic"
    
}

class ExoticEntry {
    var name1 = "public"
    
    private var name2 = "private"
    
    val name5 = "publicFinal"
    
    private val name6 = "privateFinal"
  
    
    def setName2(name2:String) = {
        this.name2 = name2
    }
    
    def getName2:String = name2
    
    override def equals(that:Any) = that match {
      case other:ExoticEntry => if (name2 == null) true else name2.equals(other.name2)
      case _ => false
    }	

    override def hashCode():Int = if (name2 == null) name1.hashCode else name2.hashCode
}
