package main.scala.interpreter

object Parser {

  def parseNext(): Option[TnList] = {
    val input = State.input
    val name = State.table.nameVector.find(input.startsWith(_))
    name match {
      case Some(str) => {
        input.advance(str.length)
        Some(State.table.map(str))
      }
      case None if input.empty => {
        if(input.finished)
          State.files.pop
        else
          input.block
        parseNext
      }
      case None => None
    }
  }

  def getInput(): String = {
    State.input.queue.mkString("")
  }
}
