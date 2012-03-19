package com.zink.scala.fly

import scala.actors.Actor

/**
 * NotiFly is the interface to a Fly notify method - please excuse the pun.
 */
trait NotiFly extends FlyPrime {
  
  /**
   * @deprecated
   * Don't use this method it will be removed at version 2.0
   */
  def notify(template: AnyRef, handler: Notifiable, leaseTime: Long): Boolean
  
  /**
   * @deprecated
   * Don't use this method it will be removed at version 2.0
   */
  def notify(template: AnyRef, leaseTime: Long)(block: => Unit): Boolean
  
  /**
   * @deprecated
   * Don't use this method it will be removed at version 2.0
   */
  def notify(template: AnyRef, leaseTime: Long, actor: Actor): Boolean

  /**
   *  This method sets up a template object in the Fly Space.
   *  If an object is written to the Space in the lifetime of the
   *  notify template then the method templateMatched() will be called
   *  in the object that implements NotifyHandler.
   *
   *  Take care when setting up notify templates; a large number of
   *  pending notifies can impede 'write' performance in the space.
   *  Set the lease of the notify template to a reasonable time and renew
   *  the template lease periodically.
   *
   *  Use the NotifyLeaseRenewer helper in the examples to periodically renew leases.
   *
   * @param template - the template to match to trigger this notify handler
   * @param handler - An object that implements the Notifiable interface 
   * @param leaseTime - The time in milliseconds the template will live in the Space
   * @return boolean - Setup OK
   *
   */
  def notifyWrite(template: AnyRef, handler: Notifiable, leaseTime: Long): Boolean

  /**
   *  Notify the supplied closure when the template is matched.
   *  For example:
   *  <pre>
   *  fly.notify(template, 1000L)  {
   *   println("Block Template matched!")
   * }
   *  </pre>
   * @param template - the template to match that will result in the closure being executed
   * @param leaseTime - The time in milliseconds the template will live in the Space
   * @param block - A closure to run when the template is matched 
   * @return boolean - Setup OK
   */
  def notifyWrite(template: AnyRef, leaseTime: Long)(block: => Unit): Boolean

  /**
   *  Notify the supplied actor when the template is matched.
   *  The message sent to the actor is FlyPrime#ACTOR_MESSAGE
   *
   * @param template - the template to match that will result in the closure being executed
   * @param leaseTime - The time in milliseconds the template will live in the Space
   * @param block - An actor to send the FlyPrime#ACTOR_MESSAGE to
   * @return boolean - Setup OK
   */
  def notifyWrite(template: AnyRef, leaseTime: Long, actor: Actor): Boolean
  

   /**
   * The notifyTake method sets up a template object in the Fly Space.
   * If an object is taken from the Space in the lifetime of the 
   * notify template that matches the template, then the method 
   * templateMatched() will be called in the object that implements 
   * the interface NotifyHandler.
   * 
   * Take care when setting up notify templates; a large number of 
   * pending notifies can impede 'take' performance in the space.
   * Set the lease of the notify template to a reasonable time and renew
   * the template lease periodically.
   * 
   * Use the NotifyLeaseRenewer helper in the examples to periodically renew leases.
   * 
   * @param template - the template to match to trigger this notify
   * @param handler - An object that implements the Notifiable trait 
   * @param leaseTime - The time in milliseconds the template will live in the Space
   * @return boolean - Setup OK
   *  
   */
  def notifyTake(template:AnyRef, handler:Notifiable, leaseTime:Long):Boolean
  
  /**
   *  Call a closure when an object matching the template is taken from the space.
   *  For example:
   *  <pre>
   *  fly.notifyTake(template, 1000L)  {
   *   println("Block Template matched!")
   * }
   *  </pre>
   * @param template - the template to match that will result in the closure being executed
   * @param leaseTime - The time in milliseconds the template will live in the Space
   * @param block - A closure to run when the template is matched 
   * @return boolean - Setup OK
   */
  def notifyTake(template: AnyRef, leaseTime: Long)(block: => Unit): Boolean

  /**
   * Notify the supplied actor an object matching the template is taken from the space.
   * The message sent to the actor is FlyPrime#ACTOR_MESSAGE
   *
   * @param template - the template to match that will result in the closure being executed
   * @param leaseTime - The time in milliseconds the template will live in the Space
   * @param block - An actor to send the FlyPrime#ACTOR_MESSAGE to
   * @return boolean - Setup OK
   */
  def notifyTake(template: AnyRef, leaseTime: Long, actor: Actor): Boolean
 
}
