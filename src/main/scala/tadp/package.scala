package tadp

import scala.math._

package object pociones {

  /**
    * suerte, convencimiento, fuerza
    */
  type Niveles = (Int, Int, Int)
  type Persona = (String, Niveles)
  type Pocion = (String, List[Ingrediente])
  /**
    * nombre, cantidad, efectos
    */
  type Ingrediente = (String, Int, List[Efecto])
  type Efecto = Niveles => Niveles

  // Efectos
  val duplica: Efecto = mapNiveles(_ * 2)

  /**
    * Aplica f a cada nivel
    */
  def mapNiveles(f: Int => Int)(niveles: Niveles) =
    (f(niveles._1), f(niveles._2), f(niveles._3))

  val alMenos7: Efecto = mapNiveles(_.max(7))

  val masFuerzaSiHaySuerte: Efecto = {
    case (suerte, convencimiento, fuerza) if suerte >= 8 =>
      (suerte, convencimiento, fuerza + 5)
    case (suerte, convencimiento, fuerza) =>
      (suerte, convencimiento, fuerza - 3)
  }

  val suerteEsConvencimiento: Efecto = {
    case (suerte, convencimiento, fuerza) => (suerte, suerte, fuerza)
  }

  def invierte(niveles: Niveles): Niveles = (niveles._3, niveles._2, niveles._1)

  // Pociones
  val multijugos = ("Multijugos", List(
    ("Cuerno de Bicornio en Polvo", 10, List(invierte(_), suerteEsConvencimiento)),
    ("Sanguijuela hormonal", 54, List(duplica, suerteEsConvencimiento))
  ))

  val felixFelices = ("Felix Felices", List(
    ("Escarabajos Machacados", 52, List(duplica, alMenos7)),
    ("Ojo de Tigre Sucio", 2, List(masFuerzaSiHaySuerte))
  ))

  val floresDeBach = ("Flores de Bach", List(
    ("Orquidea Salvaje", 8, List(masFuerzaSiHaySuerte)),
    ("Rosita", 1, List(duplica))
  ))

  val pociones: List[Pocion] = List(felixFelices, multijugos, floresDeBach)

  val personas = List(
    ("Harry", (11, 5, 4)),
    ("Ron", (6, 4, 6)),
    ("Hermione", (8, 12, 2)),
    ("Draco", (7, 9, 6))
  )

  def min2(n: Int)(m: Int) = min(n, m)

  // Punto 1
  val sumaNiveles: Niveles => Int = {
    case (s, c, f) => s + c + f
  }

  implicit class FComposition[A, B](f: A => B) {
    def °[C](g: C => A): C => B = f.compose(g)

    def <-|[C](g: C => A): C => B = f.compose(g)
  }

  val diferenciaNiveles: Niveles => Int = {
    case (s, c, f) =>
      //      max(max(s,c), f) - min(min(s,c), f)
      (((_: Int).max(s)) ° ((_: Int).max(c))) (f)
      -(min2(s) _ ° min2(c)) (f)

      s.max(c).max(f) - s.min(c).min(f)
  }

  def niveles(persona: Persona) = persona._2

  //  def sumaNivelesPersona(persona: Persona) = (sumaNiveles _ ° niveles)(persona)
  val sumaNivelesPersona = sumaNiveles ° niveles

  val diferenciaNivelesPersona = diferenciaNiveles ° niveles

  // Punto 2
  def ingredientes(pocion: Pocion) = pocion._2

  def efectos(ingrediente: Ingrediente) = ingrediente._3

  def efectosDePocion(pocion: Pocion) = {
    //    ingredientes(pocion).flatMap(efectos _)
    (ingredientes _ andThen (_ flatMap (efectos _))) (pocion)
  }

  // Punto 3
  def nombrePocion(pocion: Pocion) = pocion._1

  val pocionesHeavies =
    ((_: List[Pocion]) filter (efectosDePocion _ andThen (_ size) andThen (_ >= 4))) andThen (_.map(nombrePocion(_)))

  def pocionesHeaviesForExpression(pociones: List[Pocion]) =
    for (pocion@(nombre, _) <- pociones if efectosDePocion(pocion).size >= 4) yield nombre

  // Punto 4
  def incluyeA(incluida: List[Int], incluyente: List[Int]) = incluida.forall(incluyente.contains(_))

  val vocales = List("a", "e", "i", "o", "u")
  val tieneTodasLasVocales: String => Boolean = { nombre => vocales.forall(nombre.contains(_)) }
  val esPar: Integer => Boolean = _ % 2 == 0
  val algunIngredienteTieneTodasLasVocales: List[Ingrediente] => Boolean = _.exists { ing => tieneTodasLasVocales(ing._1) }
  val todosCantidadesPares: List[Ingrediente] => Boolean = _.forall { ing => esPar(ing._2) }

  val esPocionMagica: Pocion => Boolean = {
    pocion =>
      algunIngredienteTieneTodasLasVocales(pocion._2) && todosCantidadesPares(pocion._2)
  }

  // Punto 5
  def nombre(persona: Persona) = persona._1

  def tomarPocion(pocion: Pocion, persona: Persona) = {
    efectosDePocion(pocion).foldLeft(persona) { (persona, efecto) => (nombre(persona), efecto(niveles(persona))) }
  }

  val tomarPocion2: (Pocion, Persona) => Persona =
    efectosDePocion(_).foldLeft(_) {
      case ((nombre, niveles), efecto) =>
        (nombre, efecto(niveles))
    }

  // Punto 6
  val esAntidoto: (Persona, Pocion, Pocion) => Boolean = { (persona, pocion, antidoto) =>
    tomarPocion(antidoto, tomarPocion(pocion, persona)) == persona
  }

  implicit class PocionTomable(pocion: Pocion) {
    def apply(persona: Persona): Persona = tomarPocion(pocion, persona)
  }

  def pp(p: Pocion) = p(_)

  def esAntidoto2(antidoto: Pocion, pocion: Pocion, persona: Persona) = ((persona == _) ° (antidoto(_: Persona)) ° (pocion(_: Persona))) (persona)

  //Punto 7
  def maximoF[T](f: T => Int, lista: List[T]): T =
    lista.maxBy(f)

  type Ponderacion = Niveles => Int

  val personaMasAfectada: (Pocion, Ponderacion, List[Persona]) => Persona = { (pocion, ponderacion, personas) =>
    maximoF({ p: Persona => ponderacion(tomarPocion(pocion, p)._2) }, personas)
  }

  // Punto 8
  val promedioDeNiveles: Niveles => Int = { niveles => sumaNiveles(niveles) / 3 }
  val fuerzaFisica: Niveles => Int = { niveles => niveles._3 }

}
