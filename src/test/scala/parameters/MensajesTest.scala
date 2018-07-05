package parameters

import org.scalatest.{Matchers, WordSpec}

class MensajesTest extends WordSpec with Matchers {

  "Mensajes" should {

    "usa el implicit" in {
      new Mensajes().lowerMensaje("Hola") shouldEqual "hola!"
    }

  }

}
