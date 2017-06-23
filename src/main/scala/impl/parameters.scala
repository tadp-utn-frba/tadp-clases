package impls

trait Config {
  def fin: String
}

object Parameters {

  def saluda(persona: Persona)(implicit config: Config) = s"Hola ${persona.nombre}${config.fin}"

  implicit object duda extends Config {
    def fin = "?"
  }

  implicit val admiracion = new Config {
    def fin = "!"
  }

}