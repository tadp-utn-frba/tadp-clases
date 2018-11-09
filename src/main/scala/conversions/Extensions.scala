package conversions

object Extensions {

  class Importanteador(s: String) {
    def importante: String = s + "!"
  }


















  implicit class StringImportante(val s: String) extends AnyVal {
    def pregunta: String = s + "?"
  }

}
