package conversions

import java.util.concurrent.TimeUnit

import conversions.Extensions._
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._

class ExtensionsTest extends WordSpec with Matchers {

  "Extensions" should {

    "puedo wrappear un objeto" in {
      new Importanteador("Hola").importante shouldEqual "Hola!"
    }

    "puedo agregar importante a String" in {
      implicit def stringToImportanteador(string: String): Importanteador =
        new Importanteador(string)

      "Hola".importante shouldEqual "Hola!"
    }

    "puedo agregar pregunta a String" in {
      "Hola".pregunta shouldEqual "Hola?"
    }

    "duration" in {
      val manual = new FiniteDuration(2, TimeUnit.SECONDS)
      val infixExtension = 2 seconds

      manual shouldEqual infixExtension
    }
  }

}
