package interpreter
import java.io._

import collection.mutable.Queue

trait InputQueue {
  def startsWith(str: String): Boolean;
  def advance(len: Int): Unit;
  def block(): Unit;
  def finished(): Boolean
}

class FileInputQueue(filename: String) extends InputQueue {
  val reader = new FileReader(filename)
  val queue = Queue[Char]()
  while(reader.ready) queue += reader.read.toChar
  queue += 0x4

  override def startsWith(str: String) = queue.startsWith(str)

  override def advance(len: Int) = for(_ <- 1 to len) queue.dequeue

  override def finished = queue.isEmpty
  override def block() = throw new EOFException()
}

object StdInReader extends InputQueue {
  val reader = new InputStreamReader(System.in)
  val queue = Queue[Char]()
  def read() = while(reader.ready) queue += reader.read.toChar

  override def startsWith(str: String) = {
    read
    queue.startsWith(str)
  }

  override def advance(len: Int) = for(_ <- 1 to len) queue.dequeue

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