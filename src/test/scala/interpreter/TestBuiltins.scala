package interpreter

import java.nio.file.{Files, Paths}

import main.scala.interpreter._

import collection.mutable.Stack
import collection.JavaConversions._
import TestHelpers._
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}

class TestBuiltins extends FunSuite with BeforeAndAfterAll with BeforeAndAfter {
  val baseDir = "src/test/resources/"
  val basePath = Paths.get(baseDir)

  def provideFile(name: String, content: String) = {
    val path = basePath.resolve(name)
    Files.createFile(path)
    Files.write(path, content.map(_.toByte).toArray)
  }

  override def beforeAll() = {
    Files.createDirectories(basePath)
  }

  override def afterAll() = {
    for(file <- Files.newDirectoryStream(basePath))
      Files.deleteIfExists(file)
    Files.deleteIfExists(basePath)
  }

  after {
    State.files.clear
    State.files.push(StdInReader)
  }

  test("Input must close files after using them") {
    provideFile("empty.tn", "")
    giveLine(" ")
    leavesStack(TnList(Input))(baseDir + "empty.tn")()
    Parser.parseNext()
    Parser.parseNext()
    assert(State.files === Stack(DummyFile(" "), StdInReader))
  }

  test("Input should make the parser parse given file") {
    provideFile("test1.tn", "[]null?")
    leavesStack(TnList(Input, Pull, Pop, Uncons, Pop, Pull, Pop, Uncons, Pop))(baseDir + "test1.tn")(IsEmpty, NullList)
  }

  test("Output should allow writing to a file") {
    val path = baseDir + "testtemp.txt"
    leavesStack(TnList(Output, "testing", State.table.map("print"), I))(path)()
    assert(Files.readAllBytes(Paths.get(path)).map(_.toChar).mkString("") === "testing")
    Files.delete(Paths.get(path))
  }
}
