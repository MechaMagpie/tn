package main.scala.interpreter
import collection.mutable.Stack


trait TnObj {
  def apply(stack: Stack[TnObj]): Stack[TnObj] = stack.push(this)


  def isList = false
  def isInt = false
  /*
  except for these, they'll crash the whole thing hard if not checked for
   */
  def asChar: Char = ???
  def asString: String = ???
  def asInt: Int = ???
  def getList: List[TnObj] = ???
}
