package tadp

import org.scalatest.{Matchers, WordSpec}
import tadp.clase.Mensajes
import tadp.clase.ImplicitParametersImports._

class FormateadorTest extends WordSpec with Matchers {

  "Mensajes" should {

    "usa el implicit" in {
      new Mensajes().lowerMensaje("Hola") shouldEqual "hola!"
    }

  }

}
