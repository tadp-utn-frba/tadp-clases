package tadp.clase

import scala.annotation.implicitNotFound
import scala.collection.generic.CanBuildFrom
import scala.collection.immutable.List

trait Formateador {
  def formatear(mensaje: String): String
}

class Importante extends Formateador {
  def formatear(mensaje: String) =
    s"$mensaje!"
}

class Cruz extends Formateador {
  override def formatear(mensaje: String): String =
    s"XXX${mensaje}XXX"
}

class Padre {
  //  implicit val cruz = new Cruz
}

class Mensajes extends Padre {
  def nuevoMensaje(mensaje: String)(implicit formateador: Formateador) =
    formateador.formatear(mensaje)

  def lowerMensaje(mensaje: String)(implicit formateador: Formateador): String = {
    nuevoMensaje(mensaje.toLowerCase())
  }
}

object Collections {

  def myImplicitly[T](implicit algo: T) = algo

  def usaCBF() = {
    val lista = List(1, 2, 3)

    lazy val cbf: CanBuildFrom[List[Int], Int, _] =
      ???

//    val asdf = implicitly[Collections]

    lista.map(_ + 1)
  }

  def flatten: Option[Int] = {
    val optionNumero = Option(123)
    optionNumero.map(_ + 1)

    val optionOptionInt = Option(Option(123))
    optionOptionInt.flatten
//    optionNumero.flatten
  }

  @implicitNotFound(msg = "${A} no es string ameo.")
  trait EsString[A]

  val singleton: EsString[Unit] = new EsString[Unit] {}

  implicit def estoEsString[A <: String]: EsString[A] =
    singleton.asInstanceOf[EsString[A]]

  def esString[B](algo: B)(implicit x: EsString[B]): B =
    algo

  val noString: Int = 123
  val miString: String = "hola!"

  esString[String](miString)

  def miIgual[A, B](a: A, b: B)(implicit n: A =:= B): Boolean = {
    a == b
//    1.equals("hola")
//    1 == "hola"
  }

}