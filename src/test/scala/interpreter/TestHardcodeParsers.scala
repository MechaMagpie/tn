package interpreter

import main.scala.interpreter._
import Hardcodes._
import org.scalatest.{BeforeAndAfter, FunSuite}
import collection.mutable.Stack

/**
  * Created by erik on 1/29/17.
  */
class TestHardcodeParsers extends FunSuite with BeforeAndAfter {

  before {
    Main.setup()
    Parser.line = None; Parser.pos = 0
  }

  def leavesString(fun: TnList*)(before: TnObj*)(after: TnList): Unit = {
    val stk = Stack[TnObj](before:_*)
    fun.foreach(_.getList.foreach(_(stk)))
    assert(stk.pop.toString == after.toString)
  }

  test("\" should parse remainder of string") {
    Parser.line = Some("this is a test\"")
    leavesString(strQuote)()("this is a test")
  }

  test("[ should parse remainder of list") {
    Parser.line = Some("1 1 [ 1 ]]")
    leavesString(leftBrace)()(TnList(1,1, TnList(1)))
  }

  test("helper function pu[[ should yield a list, if given one") {
    Parser.line = Some("[[]]")
    leavesString(innerPull, TnList(Pop, Uncons, Pop))()(TnList(NullList))
  }
}
