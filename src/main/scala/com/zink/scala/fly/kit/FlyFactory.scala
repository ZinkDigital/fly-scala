package com.zink.scala.fly.kit

import com.zink.scala.fly.Fly
import com.zink.scala.fly.FlyPrime
import com.zink.scala.fly.FieldCodec
import com.zink.scala.fly.stub.FlyStub
import com.zink.scala.fly.stub.SerializingFieldCodec

object FlyFactory {

  def apply(host: String = FlyPrime.DEFAULT_HOST, fieldCodec: FieldCodec = new SerializingFieldCodec()): Fly =
    try {
      new FlyStub(host, fieldCodec)
    } catch {
      case e ⇒ throw new IllegalArgumentException("No Fly server running on " + host)
    }
}
