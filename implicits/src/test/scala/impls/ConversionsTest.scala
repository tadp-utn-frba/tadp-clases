package impls

import org.specs2.mutable.Specification

class ConversionsTest extends Specification {

  "Implicits Conversions" should {

    val adan = Persona("Adan")
    val eva = Persona("Eva")

    "saluda explicitamente" in {
      Conversions.saluda(adan) mustEqual "Hola Adan"
    }

    "persona que saluda con implicit def" in {
      import Conversions.personaQueSaluda
      adan.saluda mustEqual "Hola Adan"
    }

    "persona que saluda con implicit val function" in {
      import Conversions.personaQueSaludaPosta
      adan.saludaPosta("!") mustEqual "HOLA Adan!"
    }

    "persona que saluda con implicit class" in {
      import Conversions.saludaIndiferente
      adan.saluda mustEqual "meh"
    }

    "convertir un string a Persona" in {
      val p: Persona = "Adan"
      p mustEqual adan
    }

    "adan puede procrear con un string" in {
      val hijo = adan procrearCon "Eva"
      hijo mustEqual (adan procrearCon eva)
    }

    "una persona puede saludar" in {
      adan.saludaImplicito("!") mustEqual "Hola Adan!"
    }

  }

}
