package com.zink.scala.fly

import scala.actors.Actor
import scala.collection.JavaConverters.collectionAsScalaIterableConverter
import scala.collection.JavaConverters.seqAsJavaListConverter
import com.zink.fly.Fly
import com.zink.fly.NotifyHandler
import com.zink.fly.kit.FlyFactory
import com.zink.fly.Notifiable
import com.zink.fly.FieldCodec
import com.zink.fly.stub.SerializingFieldCodec

object ScalaFly {

  val ACTOR_MESSAGE = "message"

  def makeFly(host: String = "localhost", codec: FieldCodec = new SerializingFieldCodec()) = Option(FlyFactory.makeFly(host, codec)).map(ScalaFly(_))
}

case class ScalaFly(fly: Fly) {

  def read[T <: AnyRef](template: T, waitTime: Long): Option[T] = Option(fly.read(template, waitTime))
  def write(entry: AnyRef, leaseTime: Long): Long = fly.write(entry, leaseTime)
  def take[T <: AnyRef](template: T, waitTime: Long): Option[T] = Option(fly.take(template, waitTime))

  def writeMany(entries: Iterable[AnyRef], lease: Long): Long = fly.writeMany(entries.toList.asJava, lease)
  def readMany[T <: AnyRef](template: T, matchLimit: Long): Iterable[T] = fly.readMany(template, matchLimit).asScala
  def takeMany[T <: AnyRef](template: T, matchLimit: Long): Iterable[T] = fly.takeMany(template, matchLimit).asScala

  def notifyWrite(template: AnyRef, handler: Notifiable, leaseTime: Long): Boolean = fly.notifyWrite(template, handler, leaseTime)
  def notifyWrite(template: AnyRef, leaseTime: Long)(block: ⇒ Unit): Boolean = fly.notifyWrite(template, new Notifier(block), leaseTime)
  def notifyWrite(template: AnyRef, leaseTime: Long, actor: Actor): Boolean = notifyWrite(template, leaseTime) { actor ! ScalaFly.ACTOR_MESSAGE }

  def snapshot(template: AnyRef): AnyRef = fly.snapshot(template)

  class Notifier(f: ⇒ Unit) extends NotifyHandler {
    def templateMatched() = f
  }
}