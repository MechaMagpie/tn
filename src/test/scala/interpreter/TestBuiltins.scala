package interpreter

import java.nio.file.{Files, Paths}

import main.scala.interpreter._

import collection.mutable.Stack
import TestHelpers._
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import org.scalatest.Assertions._

class TestBuiltins extends FunSuite with BeforeAndAfterAll with BeforeAndAfter {

  after {
    State.files.clear
    State.files.push(StdInReader)
  }

  test("Input must close files after using them") {
    giveLine(" ")
    leavesStack(TnList(Input))("src/test/resources/empty.tn")()
    Parser.parseNext()
    Parser.parseNext()
    assert(State.files == Stack(DummyFile(" "), StdInReader))
  }

  test("Input should make the parser parse given file") {
    leavesStack(
      TnList(Input, Pull, Pop, Uncons, Pop, Pull, Pop, Uncons, Pop))("src/test/resources/test1.tn")(IsEmpty, NullList)
  }

  test("Output should allow writing to a file") {
    val path = "src/test/resources/testtemp.txt"
    leavesStack(TnList(Output, "testing", State.table.map("print"), I))(path)()
    assert(Files.readAllBytes(Paths.get(path)).map(_.toChar).mkString("") === "testing")
    Files.delete(Paths.get(path))
  }
}
