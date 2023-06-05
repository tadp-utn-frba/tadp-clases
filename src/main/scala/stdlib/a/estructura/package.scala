package stdlib.a

import stdlib.a.estructura.MyList

case class MyNode[+T](head: T, tail: MyNode[T] | MyNil[T]) {
  override def toString: String = s"[$head] -> $tail"
}

case class MyNil[+T]() {
  override def toString: String = "[]"
}

package object estructura {
  type MyList[+T] = MyNode[T] | MyNil[T]

  def createList[T](xs: T*): MyList[T] = {
    if (xs.isEmpty) MyNil()
    else MyNode(xs.head, createList(xs.tail: _*))
  }

  def emptyList[T](): MyList[T] = MyNil()
}
