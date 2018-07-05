package conversions

import java.util.concurrent.TimeUnit

object Extensions {

  class Importanteador(s: String) {
    def importante = s"$s!"

    def toLowerCase = "fuck!"
  }

  implicit def agregarImportanteAString(string: String): Importanteador =
    new Importanteador(string)

  implicit class StringImportante(val s: String) extends AnyVal {
    def importante2 = s"$s!"

    def esMail: Boolean = s.contains("@")
  }

  // Ejemplo de un uso en scala

  import scala.concurrent.duration._

  new FiniteDuration(2, TimeUnit.SECONDS)
  val tiempo = 2 seconds

}
