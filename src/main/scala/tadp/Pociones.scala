package tadp

object Pociones {

  case class Niveles(suerte: Int, convencimiento: Int, fuerza: Int) {
    def conFuerza(unaFuerza: Int): Niveles = copy(fuerza = unaFuerza)

    def aplicarATodos(f: Int => Int) = copy(f(suerte), f(convencimiento), f(fuerza))

    def invertir = copy(fuerza, convencimiento, suerte)
  }

  type Efecto = Niveles => Niveles

  val f1: Efecto = niveles => niveles.copy(niveles.suerte + 1, niveles.convencimiento + 2, niveles.fuerza + 3)
  val f2: Efecto = _.aplicarATodos(7.max)
  val f2alt: Efecto = niveles => niveles.aplicarATodos(unNivel => 7.max(unNivel))

  def sumarLista(l: List[Int]) = l.reduce(_ + _)

  val f3: Efecto = {
    case Niveles(s, c, f)
      if s >= 8 => Niveles(s, c, f + 5)
    case n => n.copy(fuerza = n.fuerza - 3)
  }

  val if1: Efecto = n => n.copy(suerte = n.suerte - 1, n.convencimiento - 2, n.fuerza - 3)

  val if3: Efecto = {
    case Niveles(s, c, f)
      if s >= 8 => Niveles(s, c, f - 5)
    case n => n.copy(fuerza = n.fuerza + 3)
  }

  case class Ingrediente(nombre: String, cantidadEnGramos: Int, efectos: List[Efecto])

  case class Pocion(nombre: String, ingredientes: List[Ingrediente]) extends (Persona => Persona) {
    def esHeavy: Boolean = efectos.length >= 4

    def efectos: List[Efecto] = efectosDePocion(this)

    def apply(persona: Persona): Persona = tomarPocion(this, persona)
  }

  case class Persona(nombre: String, niveles: Niveles) {
    def operaNiveles[T](operacion: Niveles => T): T = operacion(niveles)

    def aplicaEfecto(efecto: Efecto): Persona = copy(niveles = efecto(niveles))
  }

  val sumaNiveles: PartialFunction[Niveles, Int] = {
    case Niveles(s, c, f) => s + c + f
  }

  val diferenciaNiveles: Niveles => Int = {
    case Niveles(s, c, f) => s.max(c).max(f) - s.min(c).min(f)
  }

  val sumaNivelesPersona: Persona => Int = _.operaNiveles(sumaNiveles)
  val diferenciaNivelesPersona: Persona => Int = _.operaNiveles(diferenciaNiveles)

  val concatenaNiveles = (_: Persona).operaNiveles(n => s"${n.suerte}${n.convencimiento}${n.fuerza}")

  val efectosDePocion: Pocion => List[Efecto] = _.ingredientes.flatMap(_.efectos)

  object PocionHeavy {
    def unapply(pocion: Pocion): Option[String] = Option(pocion).filter(_.esHeavy).map(_.nombre)
  }

  val pocionesHeavies: List[Pocion] => List[String] = _.collect {
    // estoy matcheando con una poción
    case PocionHeavy(nombre) => nombre
    //case pocion@PocionHeavy(nombre) => nombre + pocion.ingredientes.head.nombre
  }

  val vocales = "aeiou"
  val esPocionMagica: Pocion => Boolean = pocion =>
    pocion.ingredientes.exists(i => vocales.forall(i.nombre.contains(_))) &&
      pocion.ingredientes.forall(i => i.cantidadEnGramos % 2 == 0)

  val tomarPocion: (Pocion, Persona) => Persona = (pocion, persona) =>
    persona.aplicaEfecto(efectosDePocion(pocion).reduce((f, g) => f.andThen(g)))

  /*
  val tomarPocion: Pocion => Persona => Persona = (pocion) => (persona) =>
    persona.aplicaEfecto(efectosDePocion(pocion).reduce((f, g) => f.andThen(g)))

  def tomarPocion2(pocion: Pocion)(persona: Persona): Persona =  persona.aplicaEfecto(efectosDePocion(pocion).reduce((f, g) => f.andThen(g)))
 */

  val esAntidoto: (Pocion, Pocion, Persona) => Boolean = (po1, po2, persona) => po1.andThen(po2)(persona) == persona
  //tomarPocion(po2, tomarPocion(po1, persona)) == persona

  val personaMasAfectada: (Pocion, Niveles => Int, List[Persona]) => Persona = (po, criterio, personas) =>
    personas.maxBy(po.andThen(_.niveles).andThen(criterio))
}
