package interpreter

import main.scala.interpreter._
import Hardcodes._
import TestHelpers._
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import collection.mutable.Stack

class TestHardcodeParsers extends FunSuite with BeforeAndAfter with BeforeAndAfterAll {

  override def afterAll = State.restore

  after {
    State.files.clear
    State.files.push(StdInReader)
  }


  test("\" should parse remainder of string") {
    giveLine("this is a test\"")
    leavesString(strQuote)()("this is a test")
  }

  test("\" should fully restore function table") {
    val before = State.table.fnLs.copy()
    giveLine("test\"")
    leavesString(strQuote, TnList(Pop, Sym, Copy))()(before)
  }

  test("[ should parse remainder of list") {
    giveLine("1 1 [ 1 ]]")
    leavesString(leftBrace)()(TnList(1,1, TnList(1)))
  }

  test("helper function pu[[ should yield a list, if given one") {
    giveLine("[[]]")
    leavesString(innerPull, TnList(Pop, Uncons, Pop))()(TnList(NullList))
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
