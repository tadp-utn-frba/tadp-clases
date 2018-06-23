package tadp

object Clase {

  type Niveles = (Int, Int, Int)
  type Persona = (String, Niveles)
  type Pocion = (String, List[Ingrediente])
  type Ingrediente = (String, Int, List[Efecto])
  type Efecto = Niveles => Niveles

  def duplica(niveles: Niveles): Niveles = niveles match {
    case (a, b, c) => (2 * a, 2 * b, 2 * c)
  }

  def duplica2(niveles: Niveles): Niveles =
    (niveles._1 * 2, niveles._2 * 2, niveles._3 * 2)

  val duplica3: Efecto =
    niveles => (niveles._1 * 2, niveles._2 * 2, niveles._3 * 2)

  val duplica4: Efecto = {
    case (a, b, c) => (2 * a, 2 * b, 2 * c)
  }

  val duplica5: Efecto = niveles =>
    (
      suerte(niveles) * 2,
      convencimiento(niveles) * 2,
      fuerza(niveles) * 2
    )

  val duplica6: Efecto =
    mapNiveles(_ * 2)

  val suerte: Niveles => Int = {
    case (a, _, _) => a
  }

  val convencimiento: Niveles => Int = {
    case (_, b, _) => b
  }

  val fuerza: Niveles => Int = {
    case (_, _, c) => c
  }

  val alMenos7: Efecto = niveles =>
    (7.max(niveles._1), 7.max(niveles._2), 7.max(niveles._3))

  val alMenos72: Efecto =
    mapNiveles2(math.max(7, _))(_)

  def mapNiveles2(f: (Int => Int))(niveles: Niveles) =
    (f(niveles._1), f(niveles._2), f(niveles._3))

  def mapNiveles3(f: (Int => Int)) =
    (niveles: Niveles) =>
      (f(niveles._1), f(niveles._2), f(niveles._3))

  //  (length . filter aprobado . map parcial) alumnos
  //  alumnos.map(parcial).filter(aprobado).length

  val toList: Niveles => List[Int] = niveles =>
    List(niveles._1, niveles._2, niveles._3)

  // sum . toList
  val sumaNiveles: Function1[Niveles, Int] =
    toList andThen (_.sum)

  val bla: PartialFunction[Niveles, Int] = {
    case (1, _, _) => 8
  }

  bla.isDefinedAt((1, 2, 3))
  val ble = bla.lift

  val invierte: Efecto = ???
  val suerteEsConvencimiento: Efecto = ???

  // Pociones
  val multijugos = ("Multijugos", List(
    ("Cuerno de Bicornio en Polvo", 10, List(invierte, suerteEsConvencimiento)),
    ("Sanguijuela hormonal", 54, List(duplica3, suerteEsConvencimiento))
  ))

  val felixFelices = ("Felix Felices", List(
    ("Escarabajos Machacados", 52, List(duplica3, alMenos7)),
    ("Ojo de Tigre Sucio", 2, List(suerteEsConvencimiento))
  ))

  val floresDeBach = ("Flores de Bach", List(
    ("Rosita", 8, List(duplica3))
  ))

  val pociones = List(multijugos, felixFelices, floresDeBach)

  /**
    * Una poción es heavy cuando tiene al menos 2 efectos
    */
  val esHeavy: Pocion => Boolean =
    pocion =>
      efectosDePocion(pocion)
        .size > 2

  private def efectosDePocion(pocion: Pocion) = {
    pocion._2.flatMap(ingrediente => ingrediente._3)
  }

  val pocionesHeavies: List[Pocion] => List[String] = {
    _.filter(esHeavy).map(_._1)
  }

  val pocionesHeavies2: List[Pocion] => List[String] = {
    for {
      pocion <- _ if esHeavy(pocion)
    } yield pocion._1
  }

  val pocionesHeavies3: List[Pocion] => List[String] = {
    _.collect {
      case pocion@(nombre, _) if esHeavy(pocion)
      => nombre
    }
  }

  val pocionesHeavies4: List[Pocion] => List[String] = {
    _.collect {
      case Heavy(nombre, _) => nombre
    }
  }

  val mapNiveles: (Int => Int) => Efecto =
    f => niveles =>
      (f(niveles._1), f(niveles._2), f(niveles._3))

  object Heavy {
    def unapply(pocion: Pocion)
      : Option[(String, List[Ingrediente])] = {
      Option(pocion).filter(esHeavy)
    }
  }

}
