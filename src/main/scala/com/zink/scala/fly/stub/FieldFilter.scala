package com.zink.scala.fly.stub

import java.lang.reflect.Field
import java.lang.reflect.Modifier._

/**
 *  A Filter for fields of an object we can persist.
 */
object FieldFilter {
  
  def ignored(mod:Int) = isTransient(mod) || isStatic(mod)
  
  def apply(field: Field) = !ignored(field.getModifiers)
}