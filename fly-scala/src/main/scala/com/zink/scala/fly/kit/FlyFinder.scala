package com.zink.scala.fly.kit

import com.zink.scala.fly._
import com.zink.scala.fly.stub._

object FlyFinder {
  def find() = new FlyFinder().find()
}

private class FlyFinder(fieldCodec:FieldCodec = new SerializingFieldCodec) {
  
	private val FINDING_SLEEP = 10  //ms
    
    private val FIND_MAX = 11
    private val FIND_ALL_MAX = 21
    
    private val cache = new FlyCache()
    
    /**
     * Find any Fly instance on the local sub net and return a reference to it
     *
     * @return Some fly instance or None if nothing found
     */
    def find():Option[Fly] =
        try {
            cache.start()
            var reps = cache.getAllReps
            
            // wait for any space to be found
            var sleeps = 0
            while (reps.isEmpty && sleeps < FIND_MAX) {
                if ((sleeps+1) % 5 == 0 ) {
                    cache.issueRequest()
                }
                Thread.sleep(FINDING_SLEEP)
                reps = cache.getAllReps
                sleeps += 1
            }
            
            // either something is found or we ran out of time
            if (!reps.isEmpty) Some(new FlyStub(reps.next().flyAddr, fieldCodec)) else None
        } catch {
           case e => throw new FlyAccessException(e)
        } finally {
          cache.terminate()
        }
       
    
    /**
     * Find a Fly instance on the local sub net that mathces the tag supplied 
     * in the array of tags supplied. 
     * 
     * 
     * @param tag - the tag used to match with the FlySpace tags
     * @return a reference to a Fly interface or None
     */            
    def find(tag:String):Option[Fly] = find(Array(tag))
    
    
    /**
     * Find a Fly instance on the local sub net that mathces the tags supplied 
     * in the array of tags supplied. All of the tags provided as a parameter 
     * must match those provided by the Fly instance. 
     * 
     * 
     * @param tags - the tags used to match with the FlySpace tags
     * @return a reference to a Fly interface - None if none found
     */         
    def find(tags:Array[String]):Option[Fly] =
        try {
            cache.start()
            var sleeps = 0
            var fly:Option[Fly] = None
            while (fly.isEmpty && sleeps < FIND_MAX) {
              sleeps+=1
              if ( (sleeps+1) % 5 == 0 ) {
            	  cache.issueRequest()
              }
              Thread.sleep(FINDING_SLEEP)
              fly = cache.getFirstMatchingRep(tags).map(rep => new FlyStub(rep.flyAddr, fieldCodec))
            }
          fly
        } catch {
            case e => throw new FlyAccessException(e)
        } finally {
          cache.terminate()
        }
}
