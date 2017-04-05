package interpreter
import main.scala.interpreter._
import org.scalatest.Assertions._
import collection.mutable.Stack

object TestHelpers {

  def leavesString(fun: TnList*)(before: TnObj*)(after: TnList): Unit = {
    val stk = Stack[TnObj](before:_*)
    fun.foreach(_.getList.foreach(_(stk)))
    assert(stk.pop.toString === after.toString)
  }

  def leavesStack(fun: TnList*)(before: TnObj*)(after: TnObj*): Unit = {
    val stk = Stack(before:_*)
    fun.foreach(_.eval(stk))
    assert(stk === Stack(after:_*))
  }

  implicit class withDirectEval(ls: TnList) {
    def eval(stk: Stack[TnObj]) = ls.getList.foreach(_(stk))
  }

  case class DummyFile(str: String) extends InputQueue {
    for(chr <- str) queue += chr
    override def block(): Unit = ???
    override def finished(): Boolean = throw new IllegalStateException()
  }

  def giveLine(str: String) = State.files.push(DummyFile(str));

  def testLine(str: String)(implicit stack: Stack[TnObj]) = {
    giveLine(str)
    try {
      while(true) Parser.parseNext().get.eval(stack)
    } catch {
      case _: Throwable => {}
    }
  }
}
