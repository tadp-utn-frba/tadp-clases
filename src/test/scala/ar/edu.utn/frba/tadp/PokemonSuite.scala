package ar.edu.utn.frba.tadp.pokemon

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class ActividadSuite extends FlatSpec with Matchers {

  val especieCharizard = Especie(TipoFuego, None, 1, Stats(1, 3, 5), List())
  val especieHitmonlee = Especie(TipoPelea, None, 1, Stats(1, 3, 5), List())

  "Un charizard" should "descansar y tener toda su energia máxima" in {
    val charizard: Pokemon = Pokemon(
      Stats(1, 1, 43), especieCharizard, 200)
    val estadoInicial = Normal(charizard)
    val pokemonResultante = estadoInicial.realizarActividad(Descansar)
    val pokemon = Estado.unapply(pokemonResultante).get

    assert(pokemon.energia === 200)
  }

  "Un hitmonlee" should "levantar pesas y tener 3 de experiencia" in {
    val hitmonlee = Pokemon(Stats(1, 1, 1), especieHitmonlee, 40, 1)
    val pokemonResultante = Normal(hitmonlee).realizarActividad(LevantarPesas(1))
    val pokemon = Estado.unapply(pokemonResultante).get 

    assert(pokemon.xp === 3)
  }
}
