package com.zink.scala.fly

import scala.actors.Actor
import scala.collection.JavaConverters.collectionAsScalaIterableConverter
import scala.collection.JavaConverters.seqAsJavaListConverter

import com.zink.fly.Fly
import com.zink.fly.NotifyHandler
import com.zink.fly.kit.FlyFactory
import com.zink.fly.Notifiable
import com.zink.fly.FieldCodec

import com.zink.fly.kit.FlyFactory
import com.zink.fly._

import com.zink.fly.stub.SerializingFieldCodec

object ScalaFly {

  val ACTOR_MESSAGE = "message"

  def makeFly(host: String = "localhost", codec: FieldCodec = new SerializingFieldCodec()) = Option(FlyFactory.makeFly(host, codec)).map(ScalaFly(_))
}


trait PrimaryOps {
      self : ScalaFly =>
        
	  def read[T <: AnyRef](template: T, waitTime: Long): Option[T] = Option(fly.read(template, waitTime))
	  def write(entry: AnyRef, leaseTime: Long): Long = fly.write(entry, leaseTime)
	  def take[T <: AnyRef](template: T, waitTime: Long): Option[T] = Option(fly.take(template, waitTime))
}

trait MultiOps {
      self : ScalaFly =>
        
	  def writeMany(entries: Iterable[AnyRef], lease: Long): Long = fly.writeMany(entries.toList.asJava, lease)
	  def readMany[T <: AnyRef](template: T, matchLimit: Long): Iterable[T] = fly.readMany(template, matchLimit).asScala
	  def takeMany[T <: AnyRef](template: T, matchLimit: Long): Iterable[T] = fly.takeMany(template, matchLimit).asScala
  }
  

trait NotifyOps {
	  self : ScalaFly =>
	    
	  def notifyWrite(template: AnyRef, handler: Notifiable, leaseTime: Long): Boolean = fly.notifyWrite(template, handler, leaseTime)
	  def notifyWrite(template: AnyRef, leaseTime: Long)(block: => Unit): Boolean = fly.notifyWrite(template, new Notifier(block), leaseTime)
	  def notifyWrite(template: AnyRef, leaseTime: Long, actor: Actor): Boolean = notifyWrite(template, leaseTime) { actor ! ScalaFly.ACTOR_MESSAGE }

 	  def notifyTake(template: AnyRef, handler: Notifiable, leaseTime: Long): Boolean = fly.notifyTake(template, handler, leaseTime)
	  def notifyTake(template: AnyRef, leaseTime: Long)(block: => Unit): Boolean = fly.notifyTake(template, new Notifier(block), leaseTime)
	  def notifyTake(template: AnyRef, leaseTime: Long, actor: Actor): Boolean = notifyTake(template, leaseTime) { actor ! ScalaFly.ACTOR_MESSAGE }
}

trait PerformanceOps {
    self : ScalaFly =>
      
  	def snapshot(template: AnyRef): AnyRef = fly.snapshot(template)
}
  
  
 
case class ScalaFly(fly: Fly) extends PrimaryOps with MultiOps with  NotifyOps with PerformanceOps {
  
  class Notifier(f: => Unit) extends NotifyHandler {
    def templateMatched() = f
  }
  
}


