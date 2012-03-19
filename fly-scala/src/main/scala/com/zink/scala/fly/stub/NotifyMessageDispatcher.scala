package com.zink.scala.fly.stub

import com.zink.scala.fly.{ FlyPrime, FlyAccessException }

import scala.actors.Actor
import scala.collection._

import java.io.DataInputStream

class NotifyMessageDispatcher(typeChain: TypeChain) {

  // Notify support with block message queue and 'token to handler' map   
  private val notifyContextMap = mutable.Map[Long, (Actor, Class[AnyRef])]()

  def registerHandler(notifyToken: Long, actor: Actor, clss: Class[AnyRef]) = synchronized { notifyContextMap += (notifyToken -> (actor, clss)) }

  def decodeAndQueue(notifyMode: Long, dis: DataInputStream) {
    val notifyToken = dis.readLong()
    val (handlerActor, clss) = notifyContextMap(notifyToken)

    notifyMode match {
      case FlyPrime.NOTIFY_SIMPLE ⇒ handlerActor ! FlyPrime.ACTOR_MESSAGE
      case FlyPrime.NOTIFY_WITH_OBJECT ⇒
        val entry = typeChain.readObject(clss)
        handlerActor ! entry
      case _ ⇒ throw new FlyAccessException("Notify mode does not match type of notify handler")
    }
  }
}
