package com.zink.scala.fly.kit

import java.util.logging.Level
import java.util.logging.Logger

trait Logging {

  private val log = Logger.getLogger(getClass.getName);

  def logSevere(e: Throwable) {
    log.log(Level.SEVERE, null, e)
  }

  def logSevere(message: String) {
    log.log(Level.SEVERE, message)
  }
}
