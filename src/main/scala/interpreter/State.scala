package main.scala.interpreter
import java.io._

import collection.mutable.Stack

/**
  * Created by erik on 1/13/17.
  */
object State {
  val stack = Stack[TnObj]()
  val table = FunctionTable

  var input = new BufferedReader(new InputStreamReader(System.in))
  var output = System.out
}

object FunctionTable {
  var fnLs: TnList = TnList(TnList(TnInt('m'), fromString("init"))); update

  var map = Map[String, TnList]()
  var nameVector = Vector[String]()

  private def update(): Unit = {
    map = Map[String, TnList]()
    assert(fnLs.isTable)
    for(module <- fnLs.getList; function <- module.getList.drop(2)) {
      val name :: body :: Nil = function.getList
      map += name.asString -> body.asInstanceOf[TnList]
    }
    nameVector = map.keySet.toVector.sortBy(_.length)
  }
  def defun(name:TnObj, body:TnObj): Unit = {
    require(name.isString, name + " is not a string")
    require(body.isList, body + " is not a list")
    val firstModule = fnLs.getList.head.asInstanceOf[TnList]
    val m :: moduleName :: rest = firstModule.getList
    firstModule.list = m :: moduleName :: TnList(name, body) :: rest
    update
  }

  def undefun(name:TnObj): Unit = {
    require(name.isString)
    for(mod <- fnLs.getList) {
      val module = mod.asInstanceOf[TnList]
      val m :: moduleName :: moduleBody = module.list
      module.list = m :: moduleName :: moduleBody.filter(f => f.getList.head.asString != name.asString)
    }
    update
  }

  def replace(newTable: TnList): Unit = {
    require(newTable.isTable)
    fnLs = newTable
    update
  }
}
