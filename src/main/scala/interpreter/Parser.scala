package main.scala.interpreter

/**
  * Created by erik on 1/15/17.
  */
object Parser {
  var pos: Int = 0
  var line: Option[String] = None

  def parseNext(): Option[TnList] = {
    checkLine
    line match {
      case Some(str) => {
        val name = State.table.nameVector.find(o => str.regionMatches(pos, o, 0, o.length))
        name match {
          case Some(str) => {
            pos += str.length
            Some(State.table.map(str))
          }
          case None => None
        }
      }
      case None => None
    }
  }

  private def checkLine: Unit = {
    line match {
      case Some(str) if pos == str.length => refill
      case None => refill
      case _ => {}
    }
  }

  private def refill: Unit = {
    State.input.readLine() match {
      case str: String => {line = Some(str); pos = 0}
      case null => line = None
    }
  }
}
