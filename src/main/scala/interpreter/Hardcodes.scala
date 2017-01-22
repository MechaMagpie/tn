package main.scala.interpreter

/**
  * Created by erik on 1/17/17.
  */
object Hardcodes {
  /**
    * Danger! Danger!
    */
  implicit def fromList(list: List[TnObj]): TnList = TnList(list)
  private def ref(name: String) = State.table.map(name)

  val space = TnList()
  val tab = TnList()
  val rotate = TnList(Swap, TnList(Swap), Dip, Swap)
  val dupd = TnList(TnList(Dup), Dip)
  val tnwhile = TnList()
  tnwhile.list =  List(TnList(Dup), Dip, Swap, TnList(Dup), Dip, Swap, TnList(tnwhile, I), Cons, Cons, TnList(I),
    Uncons, Pop, Swap, Cons, Cons, NullList, Ifte)
  val not = TnList(NullList, Cons, TnList(0), TnList(1), Ifte)
  val and = TnList(NullList, Cons, TnList(NullList, Cons, TnList(1), TnList(0), Ifte), TnList(0), Ifte)
  val or = TnList(not, Dip, not, I, and, I, not, I)
  val xor = TnList(NullList, Cons, TnList(NullList, Cons, TnList(0), TnList(1), Ifte),
    TnList(NullList, Cons, TnList(1), TnList(0), Ifte), Ifte)
  val newPull = TnList(); newPull.list = List(Pull, Dup, TnList(), TnList(TnList(Swap, Dup, space, ListEq),
    TnList(Pop, Pop, ref("pull")), TnList(Swap), Ifte), TnList(), Ifte)

  val spill = TnList(TnList(Dup, IsEmpty), TnList(Uncons), tnwhile, I)
  val w = TnList(TnInt('w'))
  val rightBrace = TnList(TnInt('s'))
  val leftBrace = TnList(w, TnList(ref("pull"), I, Pop, TnList(Dup, IsList), TnList(Dup, rightBrace, ListEq, not, I),
    TnList(1), Ifte), NullList, tnwhile, I, Pop, NullList, TnList(Swap, Dup, w, ListEq, not, I), TnList(Swap, Cons),
    tnwhile, I, TnInt('!'), Put, Pop)

  val functions: List[(String, TnList)] = List(
    (" ", space),
    //("\t", tab),
    ("rotate", rotate),
    ("dupd", dupd),
    ("while", tnwhile),
    ("not", not),
    ("and", and),
    ("or", or),
    ("xor", xor),
    ("]", rightBrace),
    ("[", leftBrace),
    ("pull", newPull)
  )

  def build(): Unit = {
    for((name, body) <- functions) State.table.defun(name, body)
  }
}
