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

  val i = TnList(TnList(), TnList(0), Ifte)
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

  val fold = TnList(TnList(TnList(Dup, IsEmpty, not, I), Dip, Swap),
    TnList(Dup, TnList(TnList(Uncons), Dip, Swap), Dip, TnList(TnList(I), Dip), Dip), tnwhile, I, Pop, Pop)

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
  val allAscii = (32 to 126)
  val chars = TnList(TnList(TnList(TnInt('m') :: fromString("chars") ::
    (for(i <- allAscii) yield TnList(TnList(i), TnList(i))).toList)))
  val reverse = TnList(NullList, Swap, TnList(Dup, IsEmpty, not, I), TnList(Uncons, TnList(Swap, Cons), Dip),
    tnwhile, I, Pop)
  val strQuote = TnList(Sym, "sym", chars, I, Def, NullList, TnList(Pull, Pop, I, Dup, TnInt('"'), Eq, not, I),
    TnList(Swap, Cons), tnwhile, I, Pop, reverse, I, Swap, "sym", Swap, Def)

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
    ("print", print),
    ("spill", spill),
    ("pull", newPull),
    ("pu[[", innerPull),
    ("chars", chars),
    ("reverse", reverse),
    ("fold", fold)
  )

  def build(): Unit = {
    for((name, body) <- functions) State.table.defun(name, body)
  }
}
