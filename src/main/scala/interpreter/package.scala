package main.scala

import scala.reflect.ClassTag
import scala.language.implicitConversions

package object interpreter {
  implicit def fromInt(arg: Int): TnInt = TnInt(arg)
  implicit def fromString(arg: String): TnList = new TnList(arg.toList.map(TnInt(_)));


  implicit class withTestAs(obj: Any) {
    /**
      * Tests if object is of type {@code T} and satisfies a {@code predicate} over type {@code T}
      */
    def testAs[T: ClassTag](predicate: (T) => Boolean): Boolean = obj match {
      case obj: T => predicate(obj)
      case _ => false
    }
  }
}
