package ar.edu.tadp.pokemon

import GimnasioPokemon._
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class ActividadSuite extends FlatSpec with Matchers {

  val especieCharizard = Especie(Tipos(Fuego, Some(Volador)), 1, 1, 1, 350)
  val especieHitmonlee = Especie(Tipos(Pelea), 1, 1, 1, 200)

  "Un charizard" should "descansar y tener toda su energia máxima" in {
    val charizard: Pokemon = Pokemon(
      200, 40, 40, 100, especieCharizard)

    assert(charizard.realizarActividad(descansar).energia === 200)
  }

  "Un hitmonlee" should "levantar pesas y tener 3 de experiencia" in {
    val hitmonlee = Pokemon(
      200, 40, 40, 100, especieHitmonlee)

    assert(LevantarPesas(1)(hitmonlee).experiencia === 3)
  }
}







