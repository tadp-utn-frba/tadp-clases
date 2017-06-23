package impls

import typeclasses.ContextBounds.Saludador

case class Persona(nombre: String, padre: Option[Persona] = None, madre: Option[Persona] = None) extends Ordered[Persona] {
  def procrearCon(persona: Persona) = Persona(nombre + " jr.", Some(this), Some(persona))

  override def compare(that: Persona): Int = nombre.compareTo(that.nombre)
}

object Persona {
  implicit def strToPersona(str: String) = Persona(str)

  implicit class PersonaOps(persona: Persona) {
    def saludaImplicito(end: String) = Conversions.saluda(persona) + end
  }

  implicit object PersonaSaludadora extends Saludador[Persona] {
    override def saluda(t: Persona): String = Conversions.saluda(t)
  }

  object OrdenPorNombreManual extends Ordering[Persona] {
    override def compare(x: Persona, y: Persona) = x.nombre.compareTo(y.nombre)
  }

  object OrdenPorNombreImplicito extends Ordering[Persona] {
    override def compare(x: Persona, y: Persona) = implicitly[Ordering[String]].compare(x.nombre, y.nombre)
  }

  //implicit val orden = OrdenPorNombreImplicito
}

object Conversions {

  def saluda(persona: Persona) = s"Hola ${persona.nombre}"

  implicit def personaQueSaluda(persona: Persona) = new {
    def saluda = Conversions.saluda(persona)
  }

  implicit val personaQueSaludaPosta = (persona: Persona) => new {
    def saludaPosta(end: String) = s"HOLA ${persona.nombre}$end"
  }

  implicit class saludaIndiferente(persona: Persona) {
    def saluda = "meh"
  }

}