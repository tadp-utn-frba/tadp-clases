package parameters

import scala.annotation.implicitNotFound

object Teoremas {

  def miIgual[A, B](a: A, b: B)(implicit ev: =:=[A, B]): Boolean = {
    a == b
  }
















  @implicitNotFound(msg = "${A} no es magico!")
  trait Magico[A]

  implicit val intEsMagico: Magico[Int] =
    new Magico[Int] {}

  implicit val stringEsMagico: Magico[String] =
    new Magico[String] {}

  def esMagico[B](algo: B)(implicit ev: Magico[B]): B =
    algo

}
