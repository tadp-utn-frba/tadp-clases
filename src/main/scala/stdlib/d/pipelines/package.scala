package stdlib.d

import stdlib.b.tad.{add, append}
import stdlib.c.reduccion.foldLeft

package object pipelines {
  def map[T, U](f: T => U): List[T] => List[U] =
    foldLeft(List.empty[U])((acum, elemento) => add(acum, f(elemento)))

  def filter[T](f: T => Boolean): List[T] => List[T] =
    foldLeft(List.empty[T])((acum, elemento) => if (f(elemento)) add(acum, elemento) else acum)

  def flatten[T]: List[List[T]] => List[T] =
    foldLeft(List.empty[T])((acum, lista) => append(acum, lista))
  
  def flatMap[T, U](f: T => List[U])(x: List[T]): List[U] =
    flatten(map(f)(x))
}
