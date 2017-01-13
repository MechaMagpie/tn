package main.scala.interpreter
import collection.mutable.Stack

/**
  * Created by erik on 1/11/17.
  */
trait TnObj {
  def apply(stack: Stack[TnObj]): Stack[TnObj] = stack.push(this)

  /*
  These make no sense for general objects, but I'm too lazy to put isInstanceOf asInstanceOf everywhere
  and it's more typesaaaafe~
   */
  def isChar = false
  def isString = false
  def isList = false
  def isFunction = false
  def isModule = false
  def isTable = false
  /*
  except for these, they'll crash the whole thing hard if not checked for
   */
  def asChar: Char = ???
  def asString: String = ???
  def asInt: Int = ???
  def getList: List[TnObj] = ???
}
