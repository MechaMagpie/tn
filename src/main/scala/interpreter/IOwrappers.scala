package interpreter
import java.io._

import collection.mutable.Queue

trait InputQueue {
  val queue = Queue[Char]()

  def startsWith(str: String) = queue.startsWith(str)
  def advance(len: Int) = for(_ <- 1 to len) queue.dequeue
  def block(): Unit
  def empty() = queue.isEmpty
  def finished() = empty
}

case class FileInputQueue(filename: String) extends InputQueue {
  val reader = new FileReader(filename)
  while(reader.ready) queue += reader.read.toChar
  queue += 0x4

  override def block() = throw new EOFException()
}

object StdInReader extends InputQueue {
  val reader = new InputStreamReader(System.in)

  def read() = while(reader.ready) queue += reader.read.toChar

  override def startsWith(str: String) = {
    read
    super.startsWith(str)
  }
  override val finished = false
  override def block() = queue += reader.read.toChar
}

trait Output {
  val writer: OutputStreamWriter

  def write(chr: Char): Unit = {writer.write(chr); writer.flush};
}

class FileOutput(filename: String) extends Output {
  override val writer = new FileWriter(filename)
}

object StdOutput extends Output {
  override val writer = new OutputStreamWriter(System.out)
}