package tadp

import org.scalatest.{Matchers, WordSpec}
import tadp.clase.Collections

class FlattenTest extends WordSpec with Matchers {

  "Option" should {

    "flatten Option Option Int" in {
      Collections.flatten shouldEqual Option(123)
    }

    "typesafe equals" in {
      Collections.miIgual(1, 1) shouldEqual true
      Collections.miIgual(1, 2) shouldEqual false

//      val f: Float = 1
//      val x: String = "hola"
//      Collections.miIgual(f, x) shouldEqual false
    }

  }

}
