package com.zink.scala.fly.example

object IntegerArgument {

  def apply(args: Array[String], default: Int) = if (args.length > 0) args(0).toInt else default
}
