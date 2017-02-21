package interpreter
import main.scala.interpreter._

import collection.mutable.Stack

/**
  * Created by erik on 2/3/17.
  */
object TestHelpers {

  implicit class withDirectEval(ls: TnList) {
    def eval(stk: Stack[TnObj]) = ls.getList.foreach(_(stk))
  }
}
