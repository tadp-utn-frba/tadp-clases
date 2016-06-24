package typeclasses

import impls.Persona
import org.specs2.mutable.Specification

class NumericTest  extends Specification {

  "Numeric" should {

    val adan = Persona("Adan")
    val eva = Persona("Eva")

    "puedo sumar una lista de ints" in {
      List(3, 1, 2).sum mustEqual 6
    }

    "puedo ordenar una lista de Personas" in {
      List(eva, adan).sorted mustEqual List(adan, eva)
    }

  }

}
