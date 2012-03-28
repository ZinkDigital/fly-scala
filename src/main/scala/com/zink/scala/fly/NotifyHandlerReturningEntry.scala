package com.zink.scala.fly

/*
 * This Interface Copyright (C)2006 Zink Digital Ltd 
 */

trait NotifyHandlerReturningEntry extends Notifiable {
    
   /**
     * One of the parameters to any Notify method is a class that implements
     * a Notifiable interface. NotifyHandlerReturningEntry extends Notifiable with
     * a method that is called when an object in one of the notifies queues
     * matches the notify template object, and returns a copy of the matching
     * object.
     *
     * If you don't need the returned entry then use a plain NotifyHandler
     */
    def templateMatched(entry:AnyRef)
    
}