import tadp.Pociones._

object Main extends App {

  pocionesHeavies(pociones)

  pocionesHeaviesPartial(pociones)

  nombreDePocionHeavy(felixFelices)

  nombreDePocionHeavy.isDefinedAt(felixFelices)
  nombreDePocionHeavy.isDefinedAt(floresDeBach)

  nombreDePocionHeavy.lift(felixFelices)
  nombreDePocionHeavy.lift(floresDeBach)
  nombreDePocionConFallback(felixFelices)
  nombreDePocionConFallback(floresDeBach)

//  nombreDePocionHeavy(floresDeBach)
  val nuevasPocionesHeavies: List[Pocion] => List[String] = _.collect {
    case PocionHeavy(nombre, _) => nombre
  }

  println(nuevasPocionesHeavies(pociones))

}