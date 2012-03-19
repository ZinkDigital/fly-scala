package com.zink.scala.fly.stub

import java.lang.reflect.Field


case class FieldInfo(theType: String, name: String) {
  def this(field: Field) = this (field.getType.getName, field.getName)
}
