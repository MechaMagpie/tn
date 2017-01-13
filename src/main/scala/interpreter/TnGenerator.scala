package main.scala.interpreter

/**
  * Created by erik on 1/13/17.
  */
object TnGenerator extends Iterable[String] {
  class TnGenIterator extends Iterator[String] {
    var n = 0

    def hasNext(): Boolean = true
    def next(): String = {n += 1; f"tn$n%08d"}
  }
  override def iterator: Iterator[String] = new TnGenIterator
}
