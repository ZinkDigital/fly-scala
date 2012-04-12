package com.zink.scala.fly.stub

import com.zink.scala.fly.{ FlyPrime, FieldCodec }
import com.zink.scala.fly.kit.Logging
import scala.collection._
import java.io.{ ByteArrayOutputStream, DataOutputStream }
import java.lang.reflect.{ Field, Modifier }
import sun.reflect.ReflectionFactory
import java.util.UUID

/**
 *  TypeChain is responsible for reading and writing objects to streams.
 *  It ensures that VersionLink information exists for the type being
 *  written, creating it if necessary which involves negotiating with the server to
 *  establish the layout for the type.
 *
 *  It makes use of an ObjectCodec to actually write an object's fields to or read objects from streams.
 */
class TypeChain(remoter: Remoter, fieldCodec: FieldCodec) extends Logging {

  private[this] val objCodec = new ObjectCodec(remoter, fieldCodec)
  private[this] val bos = new ByteArrayOutputStream(FlyPrime.DEFAULT_BUFFER_SIZE)
  private[this] val dos = new DataOutputStream(bos)
  private[this] val objectFactory = new EmptyObjectFactory

  /**
   *   The map of Bridges that maps between the various views of the Objects layouts
   */
  private[this] val bridges = mutable.Map[AnyRef, VersionLink]()

  /**
   *  Get Channel will initiate the negotiation with the server
   *
   *  Check if we need pass this Entry through the mapper
   *  if it doesn't match the structure of the entry in the server.
   */
  def getChannel(obj: AnyRef): Int = synchronized {
    bridges.getOrElseUpdate(obj.getClass, createBridgeFor(obj)).channel
  }

  private[this] def createBridgeFor(obj: AnyRef): VersionLink = {
    val modifiers = obj.getClass.getModifiers
    if (Modifier.isPublic(modifiers)) createBridge(ObjectLayout(obj, fieldCodec))
    else throw new IllegalArgumentException("Fly entries must be public :" + obj.getClass)
  }

  /**
   *  Chat with the fly host to establish the agreed layout.
   *  This may be a number of proposals as part of an entry negotiation
   *  but in this case this is not the case.
   */
  private[this] def createBridge(stubLayout: ObjectLayout): VersionLink = {
    bos.reset()
    dos.writeInt(FlyPrime.FLY_HEADER ^ FlyPrime.CLASS_STRUCTURE_PREAMBLE)
    stubLayout.write(dos)

    val response = remoter.sendOperation(bos.toByteArray)
    val countered = ObjectLayout(remoter, 0, fieldCodec)
    remoter.setMessageComplete()

    val channel = countered.channel
    stubLayout.channel = channel
    VersionLink(stubLayout, countered, channel, response)
  }

  def writeObject(outStream: DataOutputStream, obj: AnyRef) {
    writeIDObject(outStream, obj, None)
  }

  def writeIDObject(outStream: DataOutputStream, obj: AnyRef, id: Option[UUID]) {
    val bridge = bridges.get(obj.getClass).get
    if (bridge.evolutionResponse == 0) {
      outStream.writeInt(bridge.hostLayout.infos.size)

      id.foreach { theId ⇒
        outStream.writeLong(theId.getMostSignificantBits())
        outStream.writeLong(theId.getLeastSignificantBits())
      }

      forEachField(obj, field ⇒ objCodec.writeObject(outStream, field.get(obj)))

      outStream.flush()
    } else {
      logSevere("Objects in Fly server has a different layout to the entry: " + obj.getClass.getName)
    }
  }

  def readObject[T <: AnyRef](clss: Class[T]): Option[T] = readObject(remoter.readLong(), clss)

  def readObject[T <: AnyRef](size: Long, clss: Class[T]): Option[T] = {
    val ret = objectFactory.makeEmptyObject(clss)
    // read the UUID 
    val msb = remoter.readLong()
    val lsb = remoter.readLong()
    if (size == FlyPrime.EMPTY_ENTRY) {
      Some(ret)
    } else {
      val passingFields = forEachField(ret, field ⇒ objCodec.readObject().map(field.set(ret, _)))
      if (passingFields != size) throw new IllegalStateException("Fly Internal Type Error : filtered fields do not match");
      Some(ret)
    }
  }

  /**
   * @return number of fields the filter was applied to
   */
  private def forEachField(obj: AnyRef, f: Field ⇒ Unit): Int = {
    val fields = obj.getClass.getDeclaredFields.filter(FieldFilter(_))
    fields.foreach(field ⇒ {
      field.setAccessible(true)
      f(field)
    })
    fields.size
  }
}
