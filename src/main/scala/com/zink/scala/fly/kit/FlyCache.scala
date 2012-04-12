package com.zink.scala.fly.kit

import java.net.InetAddress
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import scala.collection.mutable.Set

/**
 * When the cache is constructed it automatically starts listening for multicast
 * packets via the Executor.
 *
 * It is possible to safely start and stop the Cache listening for requests
 * by using the 'start' and 'terminate' methods. See FlyFinder as an example.
 *
 *
 * @author nigel
 */
class FlyCache extends FlyRepHandler {

  private[this] var discoveredHandlers = Set[FlyDiscoveredHandler]()
  private[this] var lostHandlers = Set[FlyLostHandler]()
  private[this] var listener: MulticastListener = _

  private[this] val mcr = new MulticastRequestor()
  private[this] var exec: ExecutorService = _

  private[this] var reps = Map[InetAddress, FlyServerRep]()

  start()

  def getAllReps: Iterator[FlyServerRep] = reps.valuesIterator

  def getFirstMatchingRep(tags: Array[String]): Option[FlyServerRep] = reps.values.find(_.tagsMatch(tags))

  /**
   * Register a handler that wants to know if a fly instance has been started
   * @param handler
   */
  def registerDiscoveredHandler(handler: FlyDiscoveredHandler) = discoveredHandlers += handler

  /**
   * No longer interested in discoveries
   * @param handler
   */
  def removeDiscoveredHandler(handler: FlyDiscoveredHandler) = discoveredHandlers -= handler

  /**
   * Register a handler that wants to know if a fly instance has been stopped
   * @param handler
   */
  def registerLostHandler(handler: FlyLostHandler) = lostHandlers += handler

  /**
   * No longer interested in removed events
   * @param handler
   */
  def removeLostHandler(handler: FlyLostHandler) = lostHandlers -= handler

  /**
   * gets called back when a server replies
   */
  def flyRepReply(rep: FlyServerRep) {
    reps += (rep.flyAddr -> rep)
  }

  def issueRequest() {
    mcr.sendRequest()
  }

  def start() {
    synchronized {
      // start the listener that will call us back
      if (exec == null) {
        exec = Executors.newSingleThreadExecutor()
        listener = new MulticastListener(this)
        exec.submit(listener)
      }
      // and multicast ping to find all the local running spaces
      issueRequest()
    }
  }

  def terminate() {
    synchronized {
      listener.close()
      exec.shutdownNow
      exec = null
    }
  }
}
