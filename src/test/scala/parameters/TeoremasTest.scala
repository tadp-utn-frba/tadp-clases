package parameters

import org.scalatest.{Matchers, WordSpec}
import parameters.Teoremas.{esMagico, miIgual}

class TeoremasTest extends WordSpec with Matchers {

  "Option" should {

    "flatten Option Option Int" in {
      val optionNumero: Option[Int] = Some(123)
      // optionNumero.flatten

      val optionOptionInt: Option[Some[Int]] = Some(Some(123))
      optionOptionInt.flatten shouldEqual Some(123)
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
