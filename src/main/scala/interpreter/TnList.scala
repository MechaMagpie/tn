package main.scala.interpreter
import collection.mutable.Stack

/**
  * Created by erik on 1/11/17.
  */
class TnList(var list: List[TnObj]) extends TnObj{
  override val hashCode = util.Random.nextInt;

  override def equals(obj: Any): Boolean = obj match {
    case obj: TnList => obj eq this;
    case _ => false
  }

  override def isList = true

  override def isString = list.forall(_.isChar)

  override def asString: String = list.map(_.asChar).mkString

  override def isFunction = list match {
    case name :: body :: Nil if name.isString && body.isList => true
    case _ => false
  }

  override def isModule = list match {
    case TnInt('m') :: name :: fns if name.isString && fns.forall(_.isFunction) => true
    case _ => false
  }

  override def isTable = list.forall(_.isModule)

  override def getList = list

  override def toString: String = {
    import collection.immutable.Set
    def toStringRecur(obj: TnList, seen: Set[TnObj]): String = {
      if (seen.contains(obj)) "[%]"
      else if (obj.list.isEmpty) "[]"
      else if (obj.isString) obj.list.map(_.asChar).mkString("\"","","\"")
      else {
        obj.list.
          map(_ match {
            case ls: TnList => toStringRecur(ls, seen + obj);
            case o => o.toString
          }).mkString("[", " ", "]")
      }
    }
    toStringRecur(this, Set[TnObj]())
  }

  /**
    * Inter-ref preserving copy of this list, vital for deep tablefuckery
    */
  def structuralCopy(): TnList = {
    import collection.mutable.Map
    val listRefs = Map[TnList, TnList]()
    listRefs += this -> new TnList(Nil)
    def copyRecur(on: TnList): TnList = {
      val newList = new TnList(Nil)
      listRefs += on -> newList
      newList.list = for(obj <- on.list) yield obj match {
        case obj: TnList if listRefs.contains(obj) => listRefs(obj)
        case obj: TnList => copyRecur(obj)
        case _ => obj
      }
      newList
    }
    copyRecur(this)
  }
}

object TnList {
  def apply(init: TnObj*): TnList = new TnList(List(init:_*))
  def apply(init: List[TnObj]): TnList = new TnList(init)
}