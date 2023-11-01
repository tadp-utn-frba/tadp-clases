package listas.b

package object cons {
  def isEmpty[T](list: List[T]): Boolean = list match {
    case Nil => true
    case _ => false
  }

  def head[T](list: List[T]): T = list match {
    case Nil => throw new NoSuchElementException("head of empty list")
    case x :: _ => x
  }

  def tail[T](list: List[T]): List[T] = list match {
    case Nil => throw new NoSuchElementException("tail of empty list")
    case _ :: xs => xs
  }

  def init[T](list: List[T]): List[T] = list match {
    case Nil => throw new NoSuchElementException("init of empty list")
    case _ :: Nil => Nil
    case x :: xs => x::init(xs)
  }

  def last[T](list: List[T]): T = list match {
    case Nil => throw new NoSuchElementException("init of empty list")
    case x :: Nil => x
    case x :: xs => last(xs)
  }
}
