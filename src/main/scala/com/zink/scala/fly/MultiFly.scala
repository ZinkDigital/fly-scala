package com.zink.scala.fly

/**
 *  MultiFly is the interface to a Fly Space that can write, read and take
 *  multiple entries with single method calls. These methods can be used to
 *  batch up objects to and from the space which may reduce total round trip
 *  times and network usage.
 */
trait MultiFly extends FlyPrime {

  /**
   *  The writeMany method will write a collection of entry objects into the space
   *  for an amount of time given in the lease parameter. This is the time
   *  in milli-seconds that the objects will 'live' in the Space.
   *
   * @param entries - The object to put in the Space
   * @param lease - The time in milliseconds the objects will live in the Space
   * @return leaseTime - The space can return a shorter lease than the requested
   *  lease time. To be sure that your object has been leased for the given time
   *  check that the returned lease is the same as the requested lease.
   */
  def writeMany(entries: Iterable[AnyRef], lease: Long): Long

  /**
   *  The readMany method can be used to read but not remove a number of objects
   *  from the space. The template will be matched on all of its non-null fields. If
   *  fields are set to null this means that the template will match any value
   *  in that field.
   *
   *  The matchLimit is a limit to the number of objects that the method will return in
   *  the Iterable. For example if the the space contains 1000 objects that will
   *  match the template and the match limit is set to 200, then only the first
   *  200 matching objects will be returned in the collection.
   *
   *  If on the other hand the match limit is 200 and the space contains only
   *  135 matching entries then all 135 entires will be returned.
   *
   * @param template - Template to match
   * @param matchLimit - The upper limit of matched objects to return
   * @return A collection of objects that have been matched or an empty Iterable if there are no matches
   */
  def readMany[T <: AnyRef](template: T, matchLimit: Long): Iterable[T]

  /**
   *  The readMany method can be used to read but not remove a number of objects
   *  from the space. The template will be matched on all of its non-null fields. If
   *  fields are set to null this means that the template will match any value
   *  in that field.
   *
   *  The matchLimit is a limit to the number of objects that the method will return in
   *  the Iterable.
   *
   *  The ignoreInitialMatches is the number of matching object to ignore before
   *  starting to return matching objects.
   *
   *   For Example
   *
   *   If there are 175 object in the space that match the given template and the match
   *   limit is set to 100 and the ignore is set to 100 then the space will return the
   *   'last' 75 matching objects.
   *
   *  This method is functionally equivalent to the more simple readMany method with
   *  the ignore parameter set to 0.
   *
   * @param template - Template to match
   * @param matchLimit - The upper limit of matched objects to return
   * @param ignoreInitialMatches - The number of initial matches to ignore
   *   before starting to return objects
   * @return A collection of objects that have been matched or empty if there are no matches
   */
  def readMany[T <: AnyRef](template: T, matchLimit: Long, ignoreInitialMatches: Long): Iterable[T]

  /**
   *  The takeMany method can be used to take (remove) a number of objects
   *  from the space. The template will be matched on all of its non-null fields. If
   *  fields are set to null this means that the template will match any value
   *  in that field.
   *
   *  The matchLimit is a limit to the number of objects that the method will return in
   *  the Iterable. For example if the the space contains 1000 objects that will
   *  match the template and the match limit is set to 200, then only the first
   *  200 matching objects will be returned in the collection.
   *
   *  If on the other hand the match limit is 200 and the space contains only
   *  135 matching entries then all 135 entires will be returned.
   *
   * @param template - Template to match
   * @param matchLimit - The upper limit of matched objects to return
   * @return A collection of objects that have been matched or empty if there are no matches
   */
  def takeMany[T <: AnyRef](template: T, matchLimit: Long): Iterable[T]
}
