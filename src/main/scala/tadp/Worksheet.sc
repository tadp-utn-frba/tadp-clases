import tadp.Pociones._

object Worksheet {

  pocionesHeavies(pociones)

  pocionesHeaviesPartial(pociones)

  nombreDePocionHeavy(felixFelices)

  nombreDePocionHeavy.isDefinedAt(felixFelices)
  nombreDePocionHeavy.isDefinedAt(floresDeBach)

  nombreDePocionHeavy.lift(felixFelices)
  nombreDePocionHeavy.lift(floresDeBach)
  nombreDePocionConFallback(felixFelices)
  nombreDePocionConFallback(floresDeBach)

  nombreDePocionHeavy(floresDeBach)


}