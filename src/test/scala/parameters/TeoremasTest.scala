package parameters

import org.scalatest.{Matchers, WordSpec}
import parameters.Teoremas.{esMagico, miIgual}

class TeoremasTest extends WordSpec with Matchers {

  "Option" should {

    "flatten Option Option Int" in {
      val optionOptionInt: Option[Option[Int]] = Some(Some(123))
      optionOptionInt.flatten shouldEqual Some(123)

      val listListInt: List[List[Int]] = List(List(1), List(2))
      listListInt.flatten shouldEqual List(1, 2)

      val optionNumero: Option[Int] = Some(123)
      // optionNumero.flatten
    }

    "syntax sugar type operators" in {
      case class <<<[A, B](a: A, b: B)
      val x: Int <<< String = <<<(2, "algo")

      val result = x match {
        case w <<< y => w + 1
        // case w :: y => 231
      }
      result shouldEqual 3
    }

    "typesafe equals" in {
      miIgual(1, 1) shouldEqual true
      miIgual(1, 2) shouldEqual false

      // miIgual(1, "hola")
    }

    "teorema magico" in {
      // los strings son magicos
      esMagico("hola!")

      // los ints son magicos
      esMagico(123)

      // los doubles no son magicos
      // esMagico(1.3)
    }

  }

}
