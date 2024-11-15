package ar.edu.utn.frba.tadp.pokemon

import scala.util.Try

object GimnasioPokemon {

  case class Pokemon(energia: Int,
                     xp: Int,
                     energiaMaxima: Int,
                     fuerza: Int,
                     velocidad: Int,
                     especie: Especie) {
    assert(fuerza>0  && fuerza<=100)
    assert(velocidad>0  && velocidad<=100)

    def recuperarEnergia(energiaARecuperar: Int): Pokemon = {
      this.copy(
        energia=(energia+energiaARecuperar).min(this.energiaMaxima)
      )
    }

    def pierdeEnergia(energiaPerdida: Int): Pokemon = {
      this.copy(
        energia=(energia - energiaPerdida).max(0)
      )
    }

    def ganaExperiencia(xpGanada: Int) = {
      val pokemonNuevo = copy(xp= xp+xpGanada)
      if(pokemonNuevo.nivel > this.nivel) {
        pokemonNuevo.aumentarStats
      } else pokemonNuevo
    }

    def ganaVelocidad(velocidadGanada: Int): Pokemon = this.copy(velocidad= (velocidad+velocidadGanada).min(100))

    lazy val nivel = {
      //experiencia para llegar al nivel actual
      //nivel actual
      def nivelR(experienciaParaNivel: Int,
                 nivel: Int): Int = {
        val experienciaParaProximoNivel =
          2 * experienciaParaNivel + especie.resistenciaEvolutiva
        if (experienciaParaProximoNivel > xp) {
          nivel
        } else {
          nivelR(experienciaParaProximoNivel, nivel + 1)
        }
      }
      //Llamada recursiva de obtener el nivel a partir de la experiencia actual.
      nivelR(0, 1)
    }

    def aumentarStats: Pokemon = ???

    def descansar: Pokemon = this.recuperarEnergia(energiaMaxima)
  }

  type Actividad = Try[Pokemon] => Try[Pokemon]
  val descansar: Actividad = _.map(pokemon => pokemon.descansar)
  case class LevantarPesas(kilos: Int) {
      def apply(tryPokemon: Try[Pokemon]): Try[Pokemon] = {
      //Cuando un Pokémon levanta pesas,
      // gana 1 punto de experiencia por cada kilo levantado.
      // Si un Pokémon levanta más de 10 kilos
      // por cada punto de Fuerza, no gana nada de Experiencia y
      // pierde 10 de energía.
      // Los Pokémon de Tipo Pelea ganan el doble de puntos.
      // Los Pokémon de Tipo Fantasma NO PUEDEN levantar pesas (es decir, son incapaces de realizar la
      // actividad, sin importar el peso a levantar ni ningún otro factor).
        tryPokemon.map( pokemon =>
        pokemon match {
          case Fantasma(_,_) => throw new RuntimeException("Un pokemon fantasma no puede levantar peso.")
          case Pelea(_,_) => pokemon.ganaExperiencia(2*kilos)
          case _ if (kilos / pokemon.fuerza) > 10 => pokemon.pierdeEnergia(10)
          case _ => pokemon.ganaExperiencia(kilos)
        }
        )
      }
  }
  case class Nadar(minutos: Int) extends Actividad {
    def apply(pokemon:Pokemon) = {
      val pokemonEntrenado = pokemon
        .ganaExperiencia(200 * minutos)
        .pierdeEnergia(minutos)

      pokemonEntrenado match {
        case Agua(_,_) => pokemonEntrenado.ganaVelocidad(minutos / 60)
        case _ => pokemonEntrenado
      }
    }
  }

  val levantarPesasObj10 = LevantarPesas(10)
  def levantarPesas(kilos: Int)(pokemon: Pokemon): Pokemon = ???
  val levantarPesas10 = levantarPesas(10)(_)

  case class Especie(tipoPrincipal: Tipo,
                     tipoSecundario: Option[Tipo],
                     resistenciaEvolutiva: Int,
                     multiplicador: Stats) {

      def esTipo(tipo: Tipo) = {
        esTipoPrimario(tipo) || esTipoSecundario(tipo)
      }

      def esTipoPrimario(tipo: Tipo) = tipoPrincipal == tipo
      def esTipoSecundario(tipo: Tipo) = tipoSecundario.contains(tipo)
  }

  case class Stats(energiaMaxima: Int,
                             fuerza: Int,
                             velocidad: Int) {

    //TODO: definir si estos stats son solo para los multiplicadores o tambien para los modificadores del pokemon.
    // Como validamos a un pokemon si usamos stats???
    def +(otroStats: Stats): Stats = ???
    def *(otroStats: Stats): Stats = ???
  }

  trait Tipo {
    def unapply(pokemon: Pokemon): Option[(Boolean, Boolean)] = {
      val especie = pokemon.especie
      if(especie.esTipo(this))
        Some((especie.esTipoPrimario(this),
          especie.esTipoSecundario(this)))
      else None
    }
  }
  case object Agua extends Tipo
  case object Fuego extends Tipo
  case object Piedra extends Tipo
  case object Fantasma extends Tipo
  case object Pelea extends Tipo
}
