package main.scala.interpreter

case class TnInt(value: Int) extends TnObj {
  def isChar: Boolean = value > 0 && value < 127

  override def asChar = {
    require(isChar)
    value.toChar
  }

  override def asInt = value

  override def toString: String = value.toString
}
