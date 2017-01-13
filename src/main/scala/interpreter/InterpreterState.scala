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
    map = Map[String, TnList]()
    assert(fnLs.isTable)
    for(module <- fnLs.getList; function <- module.getList.tail) {
      val name :: body :: Nil = function.getList
      map += name.asString -> body.asInstanceOf[TnList]
    }
  }
  def defun(name:TnObj, body:TnObj): Unit = {
    require(name.isString && body.isList)
    val firstModule = fnLs.getList.head.asInstanceOf[TnList]
    val m :: rest = firstModule.getList
    firstModule.list = m :: TnList(name, body) :: rest
    update
  }

  def undefun(name:TnObj): Unit = {
    require(name.isString)
    for(m <- fnLs.getList) {
      val module = m.asInstanceOf[TnList]
      module.list = module.list.filter(f => f.getList.head.asString != name.asString)
    }
    update
  }
}
