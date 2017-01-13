package main.scala.interpreter

/**
  * Created by erik on 1/13/17.
  */
object Main {
  def setup(): Unit = {
    InterpreterState.table.replace(TnList(Functions.defaultModule))
  }
}
