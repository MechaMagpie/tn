package main.scala.interpreter
import collection.mutable.Stack
import scala.collection.mutable

class TnList(var list: List[TnObj]) extends TnObj {
  override val hashCode = util.Random.nextInt;

  override def equals(obj: Any): Boolean = obj match {
    case obj: TnList => obj eq this;
    case _ => false
  }

  def isString = list.forall(_.testAs[TnInt](_.isChar))

  override def asString: String = list.map(_.asChar).mkString

  def isFunction = list match {
    case name :: body :: Nil if name.testAs[TnList](_.isString) && body.isInstanceOf[TnList] => true
    case _ => false
  }

  def isModule: Boolean = list match {
    case TnInt('m') :: name :: fns if name.testAs[TnList](_.isString)
      && fns.forall(_.testAs[TnList](f => f.isFunction || f.isModule)) => true
    case _ => false
  }

  def isTable = list.forall(_.testAs[TnList](_.isModule))

  override def getList = list

  override def toString: String = {
    import collection.immutable.Set
    def toStringRecur(obj: TnList, seen: Set[TnObj]): String = {
      if (seen.contains(obj)) "[~]"
      else if (obj.list.isEmpty) "[]"
      else if (obj.isString) obj.list.map(_.asChar).mkString("\"","","\"")
      else {
        obj.list.map(
          _ match {
            case ls: TnList if State.table.nameMap.contains(ls) => "[" + State.table.nameMap(ls) + "]"
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
  def copy(): TnList = {
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