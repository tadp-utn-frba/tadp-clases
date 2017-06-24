package tadp

import org.scalatest.{Matchers, WordSpec}
import tadp.clase.ImplicitParametersImports._
import tadp.clase.{Collections, Mensajes}

class CanBuildFromTest extends WordSpec with Matchers {

  "CanBuildFrom" should {

    "usa mi cbf" in {
      Collections.usaCBF() shouldEqual List(2,3,4)
    }

  }

}
