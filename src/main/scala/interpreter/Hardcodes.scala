package main.scala.interpreter

object Hardcodes {
  private def ref(name: String) = State.table.map(name)

  val whitespace = TnList()
  val rotate = TnList(Swap, TnList(Swap), Dip, Swap)
  val dupd = TnList(TnList(Dup), Dip)
  val tnwhile = TnList()
  tnwhile.list =  List(TnList(Dup), Dip, Swap, TnList(Dup), Dip, Swap, TnList(tnwhile, I), Cons, Cons, TnList(I),
    Uncons, Pop, Swap, Cons, Cons, NullList, Ifte)
  val not = TnList(NullList, Cons, TnList(0), TnList(1), Ifte)
  val and = TnList(NullList, Cons, TnList(NullList, Cons, TnList(1), TnList(0), Ifte), TnList(Pop, 0), Ifte)
  val or = TnList(not, Dip, not, I, and, I, not, I)
  val xor = TnList(NullList, Cons, TnList(NullList, Cons, TnList(0), TnList(1), Ifte),
    TnList(NullList, Cons, TnList(1), TnList(0), Ifte), Ifte)

  val fold = TnList(TnList(TnList(Dup, IsEmpty, not, I), Dip, Swap),
    TnList(Dup, TnList(TnList(Uncons), Dip, Swap), Dip, TnList(TnList(I), Dip), Dip), tnwhile, I, Pop, Pop)

  val anyTrue = TnList(0, Swap, TnList(Dip, or, I), fold, I)

  val numTable = TnList(TnList(TnList(TnInt('m') :: fromString("numbers") ::
    (for(i <- 0 to 9) yield TnList(i.toString, TnList(i))).toList)))
  val parseNum = TnList(Sym, Swap, numTable, I, Table,
    TnList(Pull), TnList(Swap, 10, Mul, Swap, Uncons, Pop, Add), tnwhile, I, Pop, Swap, Table)

  val numbers: List[(String, TnList)] = (for(i <- 0 to 9) yield (i.toString, TnList(i, parseNum, I))).toList

  lazy val numRefs = numbers.head._2 :: ref("1") :: numbers.tail.tail.map(_._2)
  lazy val isNum = TnList(TnList(for(i <- numRefs) yield TnList(Dup, i, ListEq)), anyTrue, I)

  lazy val newPull = TnList(Pull, Dup,
    TnList(),
    TnList(TnList(Swap, Dup, whitespace, ListEq),
      TnList(Pop, Pop, ref("pull"), I), TnList(
        TnList("number?", Intern, I),
        TnList(I, NullList, Cons, Swap),
        TnList(Swap), Ifte
    ), Ifte), TnList(), Ifte)

  val print = TnList(TnList(Dup, IsEmpty, not, I), TnList(Uncons, Swap, Put), tnwhile, I, Pop)
  val spill = TnList(TnList(Dup, IsEmpty, not, I), TnList(Uncons), tnwhile, I, Pop)
  val smash = TnList(TnList(Dup, IsEmpty, not, I), TnList(Uncons, TnList(Swap, Cons), Dip), tnwhile, I, Pop)
  val reverse = TnList(NullList, Swap, TnList(Dup, IsEmpty, not, I), TnList(Uncons, TnList(Swap, Cons), Dip),
    tnwhile, I, Pop)
  val allAscii = 32 to 126
  val chars = TnList(TnList(TnList(TnInt('m') :: fromString("chars") ::
    (for(i <- allAscii) yield TnList(TnList(i), TnList(i))).toList)))
  val strQuote = TnList(Sym, chars, I, Table, NullList, TnList(Pull, Pop, I, Dup, TnInt('"'), Eq, not, I),
    TnList(Swap, Cons), tnwhile, I, Pop, reverse, I, Swap, Table)
  val w = TnList(TnInt('w'))
  val short = TnList(TnList(Dup, IsEmpty), TnList(Pop, 1), TnList(Uncons, IsEmpty, Swap, Pop), Ifte)
  lazy val leftBrace = TnList(NullList, TnList(innerPull, I, Pop, Dup, "]", Intern, ListEq, not, I),
    TnList(TnList(Dup, short, I), smash,
      TnList(Swap, Cons, TnList(I), Uncons, Pop, Swap, Cons), Ifte), tnwhile, I, Pop, reverse, I)
  lazy val innerPull = TnList(ref("pull"), I, Swap, TnList(Dup, "[", Intern, ListEq),
    TnList(Swap, Pop, I, NullList, Cons, One), TnList(TnList(Dup, "\"", Intern, ListEq),
      TnList(Swap, Pop, I, NullList, Cons, One), TnList(Swap), Ifte), Ifte)
  val rightBrace = TnList(TnInt('s'))

  val functions: List[(String, TnList)] = List(
    (" ", whitespace),
    ("\t", whitespace),
    ("\n", whitespace),
    ("\r", whitespace),
    ("\u0004", whitespace),
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
    ("smash", smash),
    ("pull", newPull),
    ("pu[[", innerPull),
    ("chars", chars),
    ("reverse", reverse),
    ("fold", fold),
    ("any", anyTrue),
    ("short?", short),
    ("\"", strQuote),
    ("parsenum", parseNum),
    ("numtable", numTable),
    ("number?", isNum)
  )

  def build(): Unit = {
    for((name, body) <- functions ++ numbers) State.table.defun(name, body)
  }
}
