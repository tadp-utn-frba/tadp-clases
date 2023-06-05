package stdlib.c

package object reduccionoptimizada {
  def foldLeft[A, B](semilla: A)(reduccion: (A, B) => A)(lista: List[B]): A = {
    var acum = semilla
    for (elem <- lista) {
      acum = reduccion(acum, elem)
    }
    acum
  }
    
  def foldRight[A, B](semilla: A)(reduccion: (B, A) => A)(lista: List[B]): A = {
    var acum = semilla
    for (elem <- lista.reverse) {
      acum = reduccion(elem, acum)
    }
    acum
  }
}
