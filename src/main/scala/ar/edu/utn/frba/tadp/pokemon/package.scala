package ar.edu.utn.frba.tadp

package object pokemon {
  implicit class ListExtensions[T](list: List[T]) {
    def maxByOption[B: Ordering](f: T => B): Option[T] = list match {
      case Nil => None
      case _ => Some(list.maxBy(f))
    }
  }
}