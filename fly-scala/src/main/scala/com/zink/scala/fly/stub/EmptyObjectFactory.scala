package com.zink.scala.fly.stub
import sun.reflect.ReflectionFactory
import java.lang.reflect.Constructor

class EmptyObjectFactory {

  // make an object of the correct type without calling the
  // object's constructor. Because the object may not have a no-args
  // ctor (but may have others we simply construct in the
  // style of a serialisable class.   
  private[this] val factory = ReflectionFactory.getReflectionFactory
  private[this] val objectCtor = classOf[AnyRef].getDeclaredConstructor()

  private[this] val ctors = scala.collection.mutable.Map[Class[_], Constructor[_]]()

  def makeEmptyObject[T](tipe: Class[T]): T = {
    val typedCtr = ctors.getOrElseUpdate(tipe, factory.newConstructorForSerialization(tipe, objectCtor))
    typedCtr.newInstance().asInstanceOf[T]
  }
}