package tadp

import scala.math._

object Pociones {

  /**
    * suerte, convencimiento, fuerza
    */
  type Niveles = (Int, Int, Int)
  type Persona = (String, Niveles)

  val personas = List(
    ("Harry", (11, 5, 4)),
    ("Ron", (6, 4, 6)),
    ("Hermione", (8, 12, 2)),
    ("Draco", (7, 9, 6))
  )

  type Efecto = Niveles => Niveles

  // Efectos
  /**
    * Aplica f a cada nivel
    */
  def mapNiveles(f: Int => Int, niveles: Niveles) =
    (f(niveles._1), f(niveles._2), f(niveles._3))

  val duplica: Efecto = mapNiveles(_ * 2, _)

  val alMenos7: Efecto = mapNiveles(_.max(7), _)

  val suerteEsConvencimiento: Efecto = niveles => (niveles._1, niveles._1, niveles._3)

  val invierte: Efecto = niveles => (niveles._3, niveles._2, niveles._1)

  val toList: Niveles => List[Int] = niveles => List(niveles._1, niveles._2, niveles._3)

  val sumaNiveles: Niveles => Int = toList.andThen(_.sum)

  val maxNivel: Niveles => Int = toList.andThen(_.max)
  val minNivel: Niveles => Int = toList.andThen(_.min)
  val diferenciaNiveles: Niveles => Int = niveles => maxNivel(niveles) - minNivel(niveles)

  def niveles(persona: Persona) = persona._2

  val sumaNivelesPersona: Persona => Int = sumaNiveles.compose(niveles)

  /**
    * nombre, cantidad, efectos
    */
  type Ingrediente = (String, Int, List[Efecto])
  type Pocion = (String, List[Ingrediente])

  // Pociones
  val multijugos = ("Multijugos", List(
    ("Cuerno de Bicornio en Polvo", 10, List(invierte, suerteEsConvencimiento)),
    ("Sanguijuela hormonal", 54, List(duplica, suerteEsConvencimiento))
  ))

  val felixFelices = ("Felix Felices", List(
    ("Escarabajos Machacados", 52, List(duplica, alMenos7)),
    ("Ojo de Tigre Sucio", 2, List(suerteEsConvencimiento))
  ))

  val floresDeBach = ("Flores de Bach", List(
    ("Rosita", 8, List(duplica))
  ))

  val pociones: List[Pocion] = List(felixFelices, multijugos, floresDeBach)

  def efectos(ingrediente: Ingrediente) = ingrediente._3

  val todosLosEfectos: List[Ingrediente] => List[Efecto] = _.flatMap(efectos)

  val ingredientes: Pocion => List[Ingrediente] = _._2

  val efectosPocion: Pocion => List[Efecto] = todosLosEfectos.compose(ingredientes)

  val esHeavy: Pocion => Boolean = efectosPocion(_).size >= 2

  def nombre(pocion: Pocion) = pocion._1

  val pocionesHeavies: List[Pocion] => List[String] = _.filter(esHeavy).map(nombre)

  val nombreDePocionHeavy: PartialFunction[Pocion, String] = {
    case (nombre, ingredientes) if todosLosEfectos(ingredientes).size >= 2 => nombre
  }

  val nombreDePocionConFallback = nombreDePocionHeavy.orElse {
    case (nombre, _) => s"$nombre no es heavy"
  }

  val pocionesHeaviesPartial: List[Pocion] => List[String] = _.collect(nombreDePocionHeavy)

  val invierte1: Efecto = niveles => (niveles._3, niveles._2, niveles._1)
  val invierte2: Efecto = {
    case (a, b, c) => (c, b, a)
  }

}
