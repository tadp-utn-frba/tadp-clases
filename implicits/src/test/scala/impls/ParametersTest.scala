package impls

import org.specs2.mutable.Specification

class ParametersTest extends Specification {

  "Implicits Parameters" should {

    val adan = Persona("Adan")
    val eva = Persona("Eva")

    "saluda con duda implicita" in {
      import Parameters.duda
      Parameters.saluda(adan) mustEqual "Hola Adan?"
    }

    "saluda con sorpresa" in {
      import Parameters.admiracion
      Parameters.saluda(adan) mustEqual "Hola Adan!"
    }

    "saluda custom" in {
      val config = new Config {
        def fin = " :)"
      }
      Parameters.saluda(adan)(config) mustEqual "Hola Adan :)"

      def chainDeImplicits(persona: Persona)(implicit config: Config) =
        Parameters.saluda(persona)

      implicit val feliz = config
      chainDeImplicits(adan) mustEqual "Hola Adan :)"
    }

  }

}
