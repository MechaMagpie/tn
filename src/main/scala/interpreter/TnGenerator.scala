package main.scala.interpreter

object TnGenerator extends Iterable[String] {
  class TnGenIterator extends Iterator[String] {
    var n = 0

    def hasNext(): Boolean = true
    def next(): String = {n += 1; f"tn$n%08d"}
  }
  override def iterator: Iterator[String] = new TnGenIterator
}
