package interpreter

import main.scala.interpreter._
import Hardcodes._
import TestHelpers._
import org.scalatest.{BeforeAndAfter, FunSuite}
import collection.mutable.Stack

class TestHardcodeParsers extends FunSuite with BeforeAndAfter {

  def leavesString(fun: TnList*)(before: TnObj*)(after: TnList): Unit = {
    val stk = Stack[TnObj](before:_*)
    fun.foreach(_.getList.foreach(_(stk)))
    assert(stk.pop.toString == after.toString)
  }

  def giveLine(str: String) = Parser.line = Some(str)

  before {
    Main.setup()
    Parser.line = None; Parser.pos = 0
  }


  test("\" should parse remainder of string") {
    giveLine("this is a test\"")
    leavesString(strQuote)()("this is a test")
  }

  test("\" should fully restore function table") {
    val before = State.table.fnLs.copy()
    giveLine("test\"")
    leavesString(strQuote, TnList(Pop, Sym))()(before)
  }

  test("[ should parse remainder of list") {
    giveLine("1 1 [ 1 ]]")
    leavesString(leftBrace)()(TnList(1,1, TnList(1)))
  }

  test("helper function pu[[ should yield a list, if given one") {
    giveLine("[[]]")
    leavesString(innerPull, TnList(Pop, Uncons, Pop))()(TnList(NullList))
  }

  test("list parser should unquote given function") {
    giveLine("not]")
    leavesString(leftBrace)()(not)
  }

  test("lists may contain strings") {
    giveLine("[\"www\"]")
    leavesString(innerPull, TnList(Pop, Uncons, Pop))()(TnList("www"))
  }

  test("lists may contain numbers") {
    giveLine("[1 [23]]")
    leavesString(innerPull, TnList(Pop, Uncons, Pop))()(TnList(1, TnList(23)))
  }
}
