package ar.edu.utn.frba.tadp.pokemon

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class ActividadSuite extends FlatSpec with Matchers {

  val especieCharizard = Especie()
  val especieHitmonlee = Especie()

  "Un charizard" should "descansar y tener toda su energia máxima" in {
    val charizard: Pokemon = Pokemon()

    assert(pokemon.energia === 200)
  }

  "Un hitmonlee" should "levantar pesas y tener 3 de experiencia" in {
    val hitmonlee = Pokemon()

    assert(pokemon.xp === 3)
  }
}
