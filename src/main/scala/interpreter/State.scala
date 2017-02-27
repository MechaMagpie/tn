package main.scala.interpreter
import java.io._

import interpreter.{InputQueue, Output, StdInReader, StdOutput}

import collection.mutable.Stack

object State {
  val stack = Stack[TnObj]()
  val table = FunctionTable
  table.replace(TnList(Functions.defaultModule))
  Hardcodes.build()
  val safe = table.fnLs.copy

  def restore(): Unit = table.replace(safe.copy)

  val files = Stack[InputQueue](StdInReader)

  def input = files.head
  var output: Output = StdOutput
}

object FunctionTable {
  var fnLs: TnList = TnList(TnList(TnInt('m'), fromString("init"))); update

  var map = Map[String, TnList]()
  var nameVector = Vector[String]()
  var nameMap = Map[TnList, String]()

  private def update(): Unit = {
    map = Map[String, TnList]()
    nameMap = Map[TnList, String]()
    assert(fnLs.isTable)
    def processModule(mod: TnObj): Unit = {
      for(member <- mod.getList.drop(2)) member match {
        case function if member.isFunction => {
          val name :: body :: Nil = function.getList
          map += name.asString -> body.asInstanceOf[TnList]
          nameMap += body.asInstanceOf[TnList] -> name.asString
        }
        case module if member.isModule => processModule(module)
      }
    }
    for(module <- fnLs.getList) processModule(module)
    nameVector = map.keySet.toVector.sortBy(-_.length)
  }

  def defun(name:TnObj, body:TnObj): Unit = {
    require(name.isString, name + " is not a string")
    require(body.isList, body + " is not a list")
    if (map.keySet.contains(name.asString)) {
      map(name.asString).list = body.getList
    } else {
      val firstModule = fnLs.getList.head.asInstanceOf[TnList]
      val m :: moduleName :: rest = firstModule.getList
      firstModule.list = m :: moduleName :: TnList(name, body) :: rest
      update
    }
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
