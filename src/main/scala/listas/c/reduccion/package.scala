package listas.c

import listas.b.tad.{head, init, isEmpty, last, tail}

package object reduccion {
  def foldLeft[A, B](semilla: A)(reduccion: (A, B) => A)(lista: List[B]): A =
    if (isEmpty(lista)) semilla
    else foldLeft(reduccion(semilla, head(lista)))(reduccion)(tail(lista))
  
  def foldRight[A, B](semilla: A)(reduccion: (B, A) => A)(lista: List[B]): A =
    if (isEmpty(lista)) semilla
    else foldRight(reduccion(last(lista), semilla))(reduccion)(init(lista))
}
