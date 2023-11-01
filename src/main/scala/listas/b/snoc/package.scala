package listas.b

package object snoc {
  def isEmpty[T](list: List[T]): Boolean = list match {
    case Nil => true
    case _ => false
  }

  def head[T](list: List[T]): T = list match {
    case Nil => throw new NoSuchElementException("head of empty list")
    case Nil :+ x => x
    case xs :+ x => head(xs)
  }

  def tail[T](list: List[T]): List[T] = list match {
    case Nil => throw new NoSuchElementException("tail of empty list")
    case Nil :+ _ => Nil
    case xs :+ x => tail(xs) :+ x
  }

  def init[T](list: List[T]): List[T] = list match {
    case Nil => throw new NoSuchElementException("init of empty list")
    case xs :+ _ => xs
  }

  def last[T](list: List[T]): T = list match {
    case Nil => throw new NoSuchElementException("last of empty list")
    case xs :+ x => x
  }
}
