package listas.d

import listas.c.reduccion.foldLeft

package object ordensuperior {
  def map[T, U](f: T => U): List[T] => List[U] =
    foldLeft(List.empty[U])((lista, elemento) => f(elemento)::lista)

  def filter[T](f: T => Boolean): List[T] => List[T] =
    foldLeft(List.empty[T])((lista, elemento) =>
      if (f(elemento)) elemento::lista
      else lista
    )

  def flatMap[T, U](f: T => List[U]): List[T] => List[U] =
    foldLeft(List.empty[U])((lista, elemento) =>
      foldLeft[List[U], U](lista)((lista, elemento) => elemento::lista)(f(elemento))
    )
}
