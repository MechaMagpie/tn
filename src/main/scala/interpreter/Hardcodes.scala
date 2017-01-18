package main.scala.interpreter

/**
  * Created by erik on 1/17/17.
  */
object Hardcodes {
  /**
    * Danger! Danger!
    */
  implicit def fromList(list: List[TnObj]): TnList = TnList(list)

  val functions: List[(String, TnList)] = List(
    (" ", TnList()),
    ("\t", TnList()),
    ("while", {
      val l = TnList();
      l.list = List(TnList(Dup), Dip, Swap, TnList(Dup), Dip, Swap,
        TnList(l, I), Cons, Cons, TnList(I), Uncons, Pop, Swap, Cons, Cons, NullList, Ifte); l})
  )

  def build(): Unit = {
    for((name, body) <- functions) State.table.defun(name, body)
  }
}
