package main.scala

/**
  * Created by erik on 1/12/17.
  */
package object interpreter {
  implicit def fromInt(arg: Int): TnInt = TnInt(arg)
  implicit def fromString(arg: String): TnList = new TnList(arg.toList.map(TnInt(_)));
}
