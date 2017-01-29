package main.scala.interpreter

object Main {
  def setup(): Unit = {
    State.table.replace(TnList(Functions.defaultModule))
    Hardcodes.build;
  }
}
