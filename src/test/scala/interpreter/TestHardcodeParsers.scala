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

  test("pull should work with numbers") {
    giveLine("23 ")
    leavesStack(State.table.map("pull"), TnList(Pop, Uncons, Pop))()(23)
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
    val u = State;
    val w = Hardcodes.leftBrace
    giveLine("1 1 [ 1 ]]")
    leavesString(leftBrace)()(TnList(1,1, TnList(1)))
  }

  test("[ should not unquote results longer than one") {
    giveLine("number?]")
    leavesString(leftBrace)()(TnList(TnList(State.table.map("number?").list), I))
  }

  test("[ should unquote results with a single member") {
    giveLine("%]")
    leavesString(leftBrace)()(TnList(Mod))
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
