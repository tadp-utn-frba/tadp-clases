package tadp

import org.scalatest.funspec.AnyFunSpec

import Pociones._

class PocionesSpec extends AnyFunSpec {

  val harry = Persona("Harry", Niveles(11, 5, 4))

  val felixFelices = Pocion("Felix Felices", List(
    Ingrediente("Escarabajos Machacados", 52, List(f1, f2)),
    Ingrediente("Ojo de Tigre Sucio", 2, List(f3)),
  ))

  val unEfecto: Efecto = n => n.copy(convencimiento = n.suerte)

  val multijugos = Pocion("Multijugos", List(
    Ingrediente("Cuerno de Bicornio en Polvo", 10, List(_.invertir, unEfecto)),
    Ingrediente("Sangijuela hormonal", 54, List(_.aplicarATodos(_ * 2), unEfecto)),
  ))

  val floresDeBach = Pocion("Flores de Bach", List(
    Ingrediente("Orquidea Salvaje", 8, List(f3)),
    Ingrediente("Rosita", 1, List(f1))
  ))

  val antidotoParaBach = Pocion("Antidoto Flores de Bach", List(
    Ingrediente("Azulita", 8, List(if1)),
    Ingrediente("Orquidea Mansa", 1, List(if3))
  ))

  val misPociones = List(felixFelices, multijugos, floresDeBach)

  describe("pociones") {
    it("puede sumar niveles") {
      assert(sumaNiveles(Niveles(1, 2, 3)) === 6)
    }

    it("puede calcular diferencia de niveles") {
      assert(diferenciaNiveles(Niveles(3, 2, 9)) == 7)
    }

    it("puede sumar niveles persona") {
      assert(sumaNivelesPersona(harry) == 20)
    }
    it("puede calcular diferencia de niveles persona") {
      assert(diferenciaNivelesPersona(harry) == 7)
    }

    it("puede concatenar niveles") {
      assert(concatenaNiveles(harry) == "1154")
    }

    it("prueba copy") {
      val n = Niveles(1, 2, 3)
      assert(n.conFuerza(n.fuerza - 1) === Niveles(1, 2, 2))
    }

    it("puede calcular efectos de pocion") {
      assert(efectosDePocion(felixFelices) == List(f1, f2, f3))
    }

    it("identifica pociones heavies") {
      assert(pocionesHeavies(misPociones) == List("Multijugos"))
    }

    it("toma pocion") {
      val tomarFelixFelices = tomarPocion.curried(felixFelices)
      assert(felixFelices(harry).niveles == Niveles(12, 7, 12))
      assert(tomarFelixFelices(harry).niveles == Niveles(12, 7, 12))
    }

    it("identifica antidotos") {
      assert(esAntidoto(floresDeBach, antidotoParaBach, harry))
      assert(!esAntidoto(floresDeBach, multijugos, harry))
    }

    it("identifica persona mas afectada") {
      val harrySinSuerte = harry.copy(nombre = "harry-sin-suerte", niveles = harry.niveles.copy(suerte = 0))
      val harryBuffeado = harry.copy(nombre = "harry-buffeado", niveles = Niveles(99, 99, 99))
      assert(personaMasAfectada(
        felixFelices, sumaNiveles, List(harry, harrySinSuerte, harryBuffeado)) == harryBuffeado)
    }

  }
}
