package parameters

import org.scalatest.{Matchers, WordSpec}
import parameters.Teoremas.esMagico

class TeoremasTest extends WordSpec with Matchers {

  "Option" should {

    "flatten Option Option Int" in {
      val optionNumero = Option(123)
      optionNumero.map(_ + 1)
      // optionNumero.flatten

      val optionOptionInt = Option(Option(123))
      optionOptionInt.flatten shouldEqual Option(123)
    }

    "typesafe equals" in {
      Teoremas.miIgual(1, 1) shouldEqual true
      Teoremas.miIgual(1, 2) shouldEqual false

      // val f: Float = 1
      // val x: String = "hola"
      // Teoremas.miIgual(f, x) shouldEqual false
    }

    "teorema magico" in {
      val noMagico = 123
      val magico = "hola!"
      val unitMagico = ()

      esMagico(magico)
    }

  }

}
