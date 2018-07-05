package parameters

import scala.annotation.implicitNotFound

object Teoremas {

  def miIgual[A, B](a: A, b: B)(implicit n: A =:= B): Boolean = {
    a == b
    //    1.equals("hola")
    //    1 == "hola"
  }

  @implicitNotFound(msg = "${A} no es magico!")
  trait Magico[A]

  implicit val singleton: Magico[Unit] = new Magico[Unit] {}

  implicit def estoEsString[A <: String]: Magico[A] =
    singleton.asInstanceOf[Magico[A]]

  def esMagico[B](algo: B)(implicit x: Magico[B]): B =
    algo

}
