package interpreter

import main.scala.interpreter.Hardcodes._
import main.scala.interpreter._
import TestHelpers._
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.collection.mutable.Stack

class TestHardcodeUtils extends FunSuite with BeforeAndAfterAll {

  override def afterAll() = {
    State.restore
  }

  test("rotate should rotate top of stack") {
    leavesStack(rotate)(1,2,3,4)(3,2,1,4)
  }

  test("dupd should duplicate next-to-top element") {
    leavesStack(dupd)(1,2)(1,2,2)
  }

  test("not should invert top value") {
    leavesStack(not)(10,0)(0,0)
  }

  test("and should replace both top elements with their conjunction") {
    leavesStack(and)(1,2,3)(1,3)
  }

  test("and should always consume two values") {
    leavesStack(and)(0,1,8)(0,8)
  }

  test("or should replace both top elements with their disjunction") {
    leavesStack(or)(1,0,7)(1,7)
  }

  test("xor should do xor on top two elements") {
    leavesStack(xor)(1,0,0)(1,0)
  }

  test("xor should not be or") {
    leavesStack(xor)(1,1,9)(0,9)
  }

  test("while should loop correctly") {
    leavesStack(tnwhile)(TnList(Dup, One, Sub), TnList(Dup), 10)(0,1,2,3,4,5,6,7,8,9,10)
  }

  //TODO: find out a way to replace this value with the maximum stack depth (leaving it hardcoded for now)
  test("while should use tail-call optimized ifte and i") {
    leavesStack(tnwhile)(TnList(One, Sub), TnList(Dup), 50000)(0)
  }

  test("spill should do as expected") {
    leavesStack(spill)(TnList(1,2,3,4,5))(5,4,3,2,1)
  }

  test("fold should fold list using provided function") {
    leavesStack(fold)(TnList(Add), TnList(1,1,1,1,1), 0)(5)
  }

  test("any should yield true if any predicate holds") {
    leavesStack(anyTrue)(TnList(TnList(IsInt), TnList(IsInt), TnList(IsInt)), TnList(), TnList(), 1)(1)
  }

  test("any should yield false if no predicate holds") {
    leavesStack(anyTrue)(TnList(TnList(IsInt), TnList(IsInt), TnList(IsInt)), TnList(), TnList(), NullList)(0)
  }

  test("reverse should reverse an arbitrary list") {
    leavesString(reverse)(TnList(1,2,3,4,5))(TnList(5,4,3,2,1))
  }

  test("smash should empty a list into the list below") {
    leavesString(smash)(TnList(4,5,6), TnList(3,2,1))(TnList(6,5,4,3,2,1))
  }
}
