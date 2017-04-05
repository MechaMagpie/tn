package main.scala.interpreter

import interpreter.StdInReader

object Main {
  object ParserChoked extends Exception

  def main(args: Array[String]): Unit = {
    println("Ready!")
    try {
      while(true) {
        val fun = Parser.parseNext()
        fun match {
          case Some(list) => {
            list.getList.foreach(_(State.stack))
          }
          case _ => throw ParserChoked
        }
      }
    } catch {
      case ParserChoked => println("parser choked on input:\n" +
        Parser.getInput +
        "\nexiting...")
      case StopProgram => println("exiting...")
      case e: Exception => println("evaluation error: " + e.getMessage)
    }
  }
}
