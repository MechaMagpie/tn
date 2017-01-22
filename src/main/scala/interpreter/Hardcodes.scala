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
    TnList(Pop, Pop, ref("pull"), I), TnList(Swap), Ifte), TnList(), Ifte)

  val print = TnList(TnList(Dup, IsEmpty, not, I), TnList(Swap), tnwhile, I)
  val spill = TnList(TnList(Dup, IsEmpty, not, I), TnList(Uncons), tnwhile, I, Pop)
  val w = TnList(TnInt('w'))
  val leftBrace = TnList();
  val innerPull = TnList(ref("pull"), I, Swap, TnList(Dup, leftBrace, ListEq), TnList(Swap, Pop, I, NullList,
    Cons, One), TnList(Swap), Ifte)
  val rightBrace = TnList(TnInt('s'))
  leftBrace.list = List(w, TnList(innerPull, I, Pop, TnList(Dup, rightBrace, ListEq), TnList(0),
    TnList(spill, I, 1), Ifte), NullList, tnwhile, I, Pop, NullList, TnList(Swap, Dup, w, ListEq, not, I),
    TnList(Swap, Cons), tnwhile, I, Pop)

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
    ("print", print),
    ("]", rightBrace),
    ("[", leftBrace),
    ("spill", spill),
    ("pull", newPull),
    ("pu[[", innerPull)
  )

  def build(): Unit = {
    for((name, body) <- functions) State.table.defun(name, body)
  }
}
