package main.scala.interpreter

import collection.mutable.Stack
import scala.collection.mutable
import util.control.Breaks._

class TnFun(val name: String, function: Stack[TnObj] => Unit) extends TnObj {
  override def apply(stack: Stack[TnObj]): Stack[TnObj] = {function(stack); stack}

  override def toString = name
}

/**
  * For testing in the REPL
  */
object ConcatImplicits {
  implicit class withConcatApply(stack: Stack[TnObj]) {
    /**
      * Simulates concatenative function chaining syntax
      */
    def \(fun: TnObj): Stack[TnObj] = fun(stack)
  }
}

abstract class ArithFun(name: String, op: (Int, Int) => Int) extends
  TnFun(name, stack => {val (i2, i1) = (stack.pop.asInt, stack.pop.asInt); stack.push(op(i1, i2))})

object RecurImpl {
  def IIfte(what: TnFun): Unit = {
    var fun = what;
    breakable {
      what match {
        case Ifte => ???
        case I => ???
        case _ => ???
      }
    }
  }
}

object Dup extends TnFun("dup", stack => {val x = stack.pop; stack.push(x, x)})
object Dip extends TnFun("dip", stack => {
  val (p, x) = (stack.pop, stack.pop)
  p.getList.foreach(_(stack))
  stack.push(x)})
object Pop extends TnFun("pop", _.pop)
object I extends TnFun("i", stack => stack.pop.getList.foreach(_(stack)))
object Swap extends TnFun("swap", stack => {val (x, y) = (stack.pop, stack.pop); stack.push(x,y)})
object Cons extends
  TnFun("cons", stack => {val (a, x) = (stack.pop, stack.pop); stack.push(TnList(x::a.getList))})
object Uncons extends
  TnFun("uncons", stack => {val l = stack.pop; stack.push(l.getList.head, TnList(l.getList.tail))})
object NullList extends TnFun("[]", _.push(TnList()))
object One extends TnFun("1", _.push(1))
object Ifte extends TnFun("ifte", stack => ???)
  /*stack => {val (f, t, b) = (stack.pop, stack.pop, stack.pop);
  b.getList.foreach(_(stack));
  if(stack.pop.asInt != 0)
    t.getList.foreach(_(stack))
  else
    f.getList.foreach(_(stack))})*/ {

  override def apply(stack: mutable.Stack[TnObj]): Stack[TnObj] = {
    def lsRecur(ls: List[TnObj]): Option[TnObj] = ls match {
      case el :: Nil => Some(el)
      case el :: ls => {el(stack); lsRecur(ls)}
      case _ => None
    }
    val (f, t, b) = (stack.pop, stack.pop, stack.pop)
    b.getList.foreach(_(stack))
    if(stack.pop().asInt != 0) {
      val last = lsRecur(t.getList)
      if(last.isDefined && last.get == Ifte) Ifte(stack) else last.get(stack)
    } else {
      val last = lsRecur(f.getList)
      if(last.isDefined && last.get == Ifte) Ifte(stack) else last.get(stack)
    }
  }
}
object Def extends TnFun("def", stack => {val (body, name) = (stack.pop, stack.pop);
  if(name.asString == "sym")
    State.table.replace(body.asInstanceOf[TnList])
  else
    State.table.defun(name, body)})
object Undef extends TnFun("undef", stack => State.table.undefun(stack.pop))
object Sym extends TnFun("sym", _.push(State.table.fnLs.copy))
object Copy extends TnFun("copy", stack => stack.push(stack.pop.asInstanceOf[TnList].copy))
object Intern extends TnFun("intern", stack => stack.push(State.table.map(stack.pop.asString)))
object Pull extends TnFun("pull", stack => {
  val res = Parser.parseNext; res match {
    case Some(fun) => stack.push(fun, 1)
    case None => stack.push(TnList(), 0)
  }})
object Tn extends TnFun("tn", _.push(TnGenerator.find(!State.table.map.keySet.contains(_)).get))
object Put extends TnFun("put", stack => State.output.print(stack.pop.asInt.toChar))
object Input extends TnFun("input", stack => ???)
object Output extends TnFun("output", stack => ???)
object Exit extends TnFun("exit", stack => ???)
object Add extends ArithFun("+", _ + _)
object Sub extends ArithFun("-", _ - _)
object Mul extends ArithFun("*", _ * _)
object Div extends ArithFun("/", _ / _)
object Mod extends ArithFun("%", _ / _)
object Lt extends ArithFun("<", (i1, i2) => if (i1 < i2) 1 else 0)
object Gt extends ArithFun(">", (i1, i2) => if (i1 < i2) 1 else 0)
object Eq extends ArithFun("=", (i1, i2) => if (i1 == i2) 1 else 0)
object ListEq extends TnFun("'=", stack => {val (l1, l2) = (stack.pop, stack.pop);
  stack.push(if(l1.isList && l2.isList && l1 == l2) 1 else 0)})
object IsInt extends TnFun("int?", stack => stack.push(if(stack.pop.isInt) 1 else 0))
object IsList extends TnFun("list?", stack => stack.push(if(stack.pop.isList) 1 else 0))
object IsEmpty extends TnFun("null?", stack => stack.push(if(stack.pop.getList.isEmpty) 1 else 0))

object Functions {
  val allFunctions = List[TnFun](Dup, Dip, Pop, I, Swap, Cons, Uncons, NullList, One, Ifte, Def, Undef, Sym, Copy,
    Intern, Pull, Tn, Put, Input, Output, Exit, Add, Sub, Mul, Div, Mod, Lt, Gt, Eq, ListEq, IsInt, IsList)
  val defaultModule = new TnList(TnInt('m') :: fromString("default") ::
    (for(fun <- allFunctions) yield TnList(fun.name, TnList(fun))).toList)
}