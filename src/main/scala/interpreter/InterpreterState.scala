package main.scala.interpreter
import collection.mutable.Stack

/**
  * Created by erik on 1/13/17.
  */
object InterpreterState {
  val stack = Stack[TnObj]()
  val table = FunctionTable
}

object FunctionTable {
  var fnLs: TnList = TnList(TnList(TnInt('m'))); update

  var map = Map[String, TnList]()
  private def update(): Unit = {
    assert(fnLs.isTable)
    for(module <- fnLs.getList; function <- module.getList.tail) {
      val name :: body :: Nil = function.getList
      map += name.asString -> body.asInstanceOf[TnList]
    }
  }
}
