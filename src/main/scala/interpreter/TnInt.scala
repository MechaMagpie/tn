package main.scala.interpreter

/**
  * Created by erik on 1/11/17.
  */
case class TnInt(value: Int) extends TnObj {
  override def isChar: Boolean = value > 31 && value < 127

  override def asChar = {
    require(isChar)
    value.toChar
  }

  override def isInt = true

  override def asInt = value

  override def toString: String = value.toString
}
