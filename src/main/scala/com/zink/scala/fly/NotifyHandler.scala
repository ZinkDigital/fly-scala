package com.zink.scala.fly

trait NotifyHandler extends Notifiable {

  /**
   * One of the parameters to any notify method is a class that implements
   * a Notifiable interface. NotifyHandler simply extends Notifiable with
   * a method that is called when an object in one of the notify queues
   * matches the notify template object.
   *
   * If you need a copy of the matching entry returned then use NotifyHandler
   */
  def templateMatched()
}
