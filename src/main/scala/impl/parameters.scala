package impls

trait Config[_] {
  def fin: String
}

object Parameters {

  def saluda[_: Config](persona: Persona) = otraCosa(persona.nombre)

  def otraCosa(nombre: String)(implicit config: Config[_]) = s"Hola ${nombre}${config.fin}"

//  implicit object duda extends Config[_] {
//    def fin = "?"
//  }
//
//  implicit val admiracion = new Config[_] {
//    def fin = "!"
//  }

}