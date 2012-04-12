package com.zink.scala.fly.stub

import java.lang.reflect.Field
import java.lang.reflect.Modifier._

/**
 *  A Filter for fields of an object we can persist.
 */
object FieldFilter {
  def apply(field: Field): Boolean = {
    val modVal = field.getModifiers
    /* !isFinal(modVal) && */ 
    !isTransient(modVal) && !isStatic(modVal)
  }
}
