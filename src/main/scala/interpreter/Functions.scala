package main.scala.interpreter

import collection.mutable.Stack
import scala.collection.mutable

class TnFun(val name: String, function: Stack[TnObj] => Unit) extends TnObj {
  override def apply(stack: Stack[TnObj]): Stack[TnObj] = {function(stack); stack}

  override def toString = name
}

object Dup extends TnFun("dup", stack => {val x = stack.pop; stack.push(x, x)})
object Dip extends TnFun("dip", stack => {
  val (p, x) = (stack.pop, stack.pop)
  p.asInstanceOf[TnList].applyContent(stack)
  stack.push(x)})
object Pop extends TnFun("pop", _.pop)
object I extends TnFun("i", stack => stack.pop.asInstanceOf[TnList].applyContent(stack))
object Swap extends TnFun("swap", stack => {val (x, y) = (stack.pop, stack.pop); stack.push(x,y)})
object Cons extends
  TnFun("cons", stack => {val (a, x) = (stack.pop, stack.pop); stack.push(new TnList(x::a.getList))})
object Uncons extends
  TnFun("uncons", stack => {val l = stack.pop; stack.push(l.getList.head, new TnList(l.getList.tail))})
object NullList extends TnFun("[]", _.push(TnList()))
object One extends TnFun("1", _.push(1))

abstract class ArithFun(name: String, op: (Int, Int) => Int) extends
  TnFun(name, stack => {val (i2, i1) = (stack.pop.asInt, stack.pop.asInt); stack.push(op(i1, i2))})

object Add extends ArithFun("+", _ + _)
object Sub extends ArithFun("-", _ - _)
object Mul extends ArithFun("*", _ * _)
object Div extends ArithFun("/", _ / _)
object Mod extends ArithFun("%", _ / _)


object Functions {
  val allFunctions = List[TnFun](Add, Sub, Mul, Div, Mod)
  val defaultModule = new TnList(TnInt('m') :: (for(fun <- allFunctions) yield TnList(fun.name, TnList(fun))).toList)
}