package com.zink.scala.fly

trait FieldCodec {

  /**
   * Encode the given object into any byte array required
   * @param field
   * @return the encoded field as a byte array
   */
  def writeField(field: AnyRef): Array[Byte]

  /**
   * Decode the byte array to create the object field
   * @param fieldBytes
   * @return the object created from the byte arrays
   */
  def readField(fieldBytes: Array[Byte]): Option[AnyRef]
}
