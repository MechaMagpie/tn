package main.scala

import scala.reflect.ClassTag
import scala.language.implicitConversions

package object interpreter {
  implicit def fromInt(arg: Int): TnInt = TnInt(arg)
  implicit def fromString(arg: String): TnList = new TnList(arg.toList.map(TnInt(_)));


  implicit class withTestAs(obj: Any) {
    def testAs[T: ClassTag](predicate: (T) => Boolean): Boolean = {
      obj match {
        case obj: T => predicate(obj)
        case _ => false
      }
    }
  }
}
