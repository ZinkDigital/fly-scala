package com.zink.scala.fly.kit

import java.net.InetAddress

case class FlyServerRep(flyAddr:InetAddress, flyTags:Array[String]) {
    
    /**
     * Test if the tags in this server rep match the seach tags
     * 
     * @param The tags are being search for by the user
     * @return true if the tags matched, false otherwise
     */
    def tagsMatch(tags:Array[String]):Boolean = {
        // if the tags from the space are null it doesn't exist
        if (flyTags == null) return false
        // if the supplied tags are null then it has to match 
        if (tags == null) return true
        // run over the supplied tags check the space matches all of them.
        
        for (tag <- tags) {
          if (!flyTags.contains(tag)) return false  
        }
        true
    }
}
