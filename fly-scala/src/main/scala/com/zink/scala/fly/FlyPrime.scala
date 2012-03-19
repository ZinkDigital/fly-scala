package com.zink.scala.fly

object FlyPrime {
  val EMPTY_ENTRY = 0x7FFFFFFF

  val FLY_HEADER = 0xFAB20000

  val CLASS_STRUCTURE_PREAMBLE = 10

  //
  // Fly OpCodes (there is an order preference here)
  //
  val PING = 0
  val READ = 1
  val TAKE = 2
  val WRITE = 3
  val NOTIFY = 4

  /**although may not ever make any sense to contact the
   *   server it is reserved in the 'namespace' anyway
   */
  val SNAPSHOT = 5

  val READ_MANY = 6
  val TAKE_MANY = 7
  val WRITE_MANY = 8
  val STATS = 9
  
  val NOTIFY_WRITE = 20
  val NOTIFY_TAKE = 21 

  val NOTIFY_WRITE_OBJECT = 22
  val NOTIFY_TAKE_OBJECT = 23
  
  val NOTIFY_SIMPLE = -1L
  val NOTIFY_WITH_OBJECT = -2L

  val ACTOR_MESSAGE = "Template matched"
  
  val DEFAULT_HOST = "localhost"
  val FLY_PORT = 4396
  
  val DEFAULT_BUFFER_SIZE = 1024
}

/**
 *
 *   FlyPrime is the simple (but powerful) interface to a Fly system.
 *
 *   Use this interface to read, write and take java objects to and
 *   from the Fly server.
 *
 * @author Nigel
 *
 */
trait FlyPrime {

  /**
   *   The write method will write an entry into the fly space for the
   *   amount of time given in the lease parameter. This is the time
   *   in milliseconds that the object will 'live' in the space.
   *
   * @param entry - The  object to put in the FlyPrime
   * @param leaseTime - The time in milliseconds the object will live in the Space
   * @return leaseTime - The space can return a shorter lease than the requested 
   *   lease time. To be sure that your object has been leased for the given time
   *   check that the returned lease is the same as the requested lease.
   */
  def write(entry: AnyRef, leaseTime: Long): Long

  /**
   *   The read method can be used to read but not remove an object from the
   *   space. The template will be matched on all of its non-null fields. If
   *   fields are set to null this means that the template will match any value
   *   in that field.
   *
   *   The waitTime is the time in milliseconds that this method will block, waiting
   *   for an object to appear that matches the template.
   *
   * @param template - The object template to match in the space.
   * @param waitTime - Time in milliseconds to wait before the object is matched.
   * @return The Some(object) that has been matched or None if the template has not been 
   *   matched in the given wait time.
   */
  def read[T <: AnyRef](template: T, waitTime: Long): Option[T]

  /**
   *   The Take method uses the same matching strategy as the read method via the
   *   template, however if an object is matched under this (take) method the object
   *   is removed from the space and returned.
   *
   * @param template - The object template to match in the space.
   * @param waitTime - The time to wait in milliseconds for the template to be matched.
   * @return The Some(object) that has been matched or None if no object has been matched
   *   in the given wait time.
   */
  def take[T <: AnyRef](template: T, waitTime: Long): Option[T]

  /**
   *   Snapshot will make a copy of an object which may help the stub prepare
   *   the object for submission to the space. This is an non essential method
   *   but may be used for performance reasons, if for example a templae is not going
   *   to change and is going to be submitted many times to the space in a read or
   *   take method.
   *
   *
   * @param template object 
   * @return snapshot object
   */
  def snapshot(template: AnyRef): AnyRef
}
