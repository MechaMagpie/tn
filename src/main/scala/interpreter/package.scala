package main.scala

package object interpreter {
  implicit def fromInt(arg: Int): TnInt = TnInt(arg)
  implicit def fromString(arg: String): TnList = new TnList(arg.toList.map(TnInt(_)));
}
