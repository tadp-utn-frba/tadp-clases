package ar.edu.utn.frba.tadp.pokemon

import scala.util.Try

object GimnasioPokemon {

  case class Pokemon(energia: Int,
                     xp: Int,
                     stats: Stats,
                     especie: Especie) {

    lazy val energiaMaxima = stats.energiaMaxima
    lazy val velocidad = stats.velocidad
    lazy val fuerza = stats.fuerza

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

    def menos50PorcientoEnergia: Boolean = ???

    def ganaExperiencia(xpGanada: Int) = {
      val pokemonNuevo = copy(xp= xp+xpGanada)
      if(pokemonNuevo.nivel > this.nivel) {
        // Chequear si evoluciona por nivel y aumentar Stats
        pokemonNuevo.aumentarStats
        pokemonNuevo.especie.evolucionarPorNivel(pokemonNuevo)
      } else pokemonNuevo
    }

    def ganaVelocidad(velocidadGanada: Int): Pokemon = this.copy(stats= stats.aumentarVelocidad(velocidadGanada))

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

    def aumentarStats: Pokemon = {
      this.copy(stats=this.stats + especie.incrementos)
    }

    def descansar: Pokemon = this.recuperarEnergia(energiaMaxima)

    def evolucion(evolucion: Especie) = this.copy(especie=evolucion)
  }

  type Actividad = Try[Estado] => Try[Estado]
  val descansar: Actividad = {
    _.map {
      case Normal(pokemon) if pokemon.menos50PorcientoEnergia => Dormido(pokemon.descansar)
      case estado => estado.map(pokemon => pokemon.descansar)
    }
  }
  case class LevantarPesas(kilos: Int) {
      def apply(tryEstado: Try[Estado]): Try[Estado] = {
      //Cuando un Pokémon levanta pesas,
      // gana 1 punto de experiencia por cada kilo levantado.
      // Si un Pokémon levanta más de 10 kilos
      // por cada punto de Fuerza, no gana nada de Experiencia y
      // pierde 10 de energía.
      // Los Pokémon de Tipo Pelea ganan el doble de puntos.
      // Los Pokémon de Tipo Fantasma NO PUEDEN levantar pesas (es decir, son incapaces de realizar la
      // actividad, sin importar el peso a levantar ni ningún otro factor).
        tryEstado.map {
          case Paralizado(pokemon) => KO(pokemon)
          case estado => estado.flatMap { pokemon => pokemon match {
            case Fantasma(_, _) => throw new RuntimeException("Un pokemon fantasma no puede levantar peso.")
            case Pelea(_, _) => estado.map(_.ganaExperiencia(2 * kilos))
            case _ if (kilos / estado.pokemon.fuerza) > 10 => Paralizado(estado.pokemon.pierdeEnergia(10))
            case _ => estado.map(_.ganaExperiencia(kilos))
            }
          }
        }
      }
  }

  case class Intercambiar(_otroPokemon: Pokemon) extends Actividad {
    def apply(tryEstado: Try[Estado]): Try[Estado] = {
      tryEstado.map(_.map(pokemon => {
        pokemon.especie.condicionEvolucion match {
          case Some(Intercambio(evolucion)) => pokemon.evolucion(evolucion)
          case _ => pokemon.pierdeEnergia(10)
        }
      }))
    }
  }

  case class UsarPiedra(piedra: Piedra) extends Actividad {
    def apply(tryEstado: Try[Estado]): Try[Estado] = {
      tryEstado.map(_.map(pokemon => {
        pokemon.especie.condicionEvolucion.collect {
          case CondicionPiedra(evolucion, `piedra`) => pokemon.evolucion(evolucion)
        }.getOrElse(pokemon)}
        )
      )
    }
  }

  //  Los Pokémon con un Tipo Principal o Secundario que "pierde" contra el Tipo Agua no ganan nada de
  //  Experiencia y quedan K.O. automáticamente.
  //  Los tipos que pierden contra agua son Roca, Tierra y Fuego.
  case class Nadar(minutos: Int) extends Actividad {
    def apply(tryEstado:Try[Estado]): Try[Estado] = {
      tryEstado.map {
        estado =>
          estado.flatMap(pokemon => {
            val pokemonEntrenado = pokemon
              .ganaExperiencia(200 * minutos)
              .pierdeEnergia(minutos)

            pokemonEntrenado match {
              case Agua(_,_) => estado.envolverPokemon(pokemonEntrenado.ganaVelocidad(minutos / 60))
              case Roca(_,_) || Tierra(_,_) || Fuego(_,_) => KO(pokemon)
              case _ => estado.envolverPokemon(pokemonEntrenado)
            }
          })
      }
    }
  }


  val levantarPesasObj10 = LevantarPesas(10)
  def levantarPesas(kilos: Int)(pokemon: Pokemon): Pokemon = ???
  val levantarPesas10 = levantarPesas(10)(_)

  case class Especie(tipoPrincipal: Tipo,
                     tipoSecundario: Option[Tipo],
                     resistenciaEvolutiva: Int,
                     condicionEvolucion: Option[CondicionEvolutiva],
                     incrementos: Stats) {

      def esTipo(tipo: Tipo) = {
        esTipoPrimario(tipo) || esTipoSecundario(tipo)
      }

      def esTipoPrimario(tipo: Tipo) = tipoPrincipal == tipo
      def esTipoSecundario(tipo: Tipo) = tipoSecundario.contains(tipo)

      def evolucionarPorNivel(pokemon: Pokemon): Pokemon = {
        condicionEvolucion match {
          case Some(Nivel(evolucion, nivelAEvoluicionar)) if pokemon.nivel >= nivelAEvoluicionar => {
             pokemon.evolucion(evolucion)
          }
          case _ => pokemon
        }
      }
  }

  case class Stats(energiaMaxima: Int,
                   fuerza: Int,
                   velocidad: Int) {
    assert(fuerza>0  && fuerza<=100)
    assert(velocidad>0  && velocidad<=100)

    def aumentarVelocidad(velocidadNueva: Int): Stats = copy(velocidad= (velocidad+velocidadNueva).min(100))

    def +(otroStats: Stats): Stats = {
      copy(energiaMaxima= energiaMaxima + otroStats.energiaMaxima,
        fuerza = fuerza + otroStats.fuerza,
        velocidad = velocidad + otroStats.velocidad
      )
    }

    def *(otroStats: Stats): Stats = {
      copy(energiaMaxima= energiaMaxima * otroStats.energiaMaxima,
        fuerza = fuerza * otroStats.fuerza,
        velocidad = velocidad * otroStats.velocidad
      )
    }
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
  case object Roca extends Tipo
  case object Tierra extends Tipo
  case object Fantasma extends Tipo
  case object Pelea extends Tipo

  trait Estado {
    val pokemon : Pokemon

    def envolverPokemon(pokemon: Pokemon): Estado
    def map(f: Pokemon => Pokemon): Estado
    def flatMap(f: Pokemon => Estado): Estado
  }
  case class KO(pokemon:Pokemon) extends Estado {
    def envolverPokemon(pokemon: Pokemon) = KO(pokemon)
    def map(f: Pokemon => Pokemon) =  throw new RuntimeException("Esta K.O., no puede hacer nada.")
    def flatMap(f: Pokemon => Estado): Estado = throw new RuntimeException("Esta K.O., no puede hacer nada.")
  }
  case class Paralizado(pokemon:Pokemon) extends Estado {
    def envolverPokemon(pokemon: Pokemon) = Paralizado(pokemon)
    def map(f: Pokemon => Pokemon) = this
    def flatMap(f: Pokemon => Estado) = this
  }
  case class Dormido(pokemon:Pokemon, cantidadTurnos: Int = 3) extends Estado {
    def envolverPokemon(pokemon: Pokemon) = Dormido(pokemon)
    def map(f: Pokemon => Pokemon) = {
      if (cantidadTurnos == 1) Normal(pokemon) else Dormido(pokemon, cantidadTurnos - 1)
    }
    def flatMap(f: Pokemon => Estado) = {
      map(pokemon => f(pokemon).pokemon)
    }
  }
  case class Normal(pokemon:Pokemon) extends Estado {
    def envolverPokemon(pokemon: Pokemon) = Normal(pokemon)
    def map(f: Pokemon => Pokemon) = copy(f(pokemon))
    def flatMap(f: Pokemon => Estado) = f(pokemon)
  }

  trait CondicionEvolutiva {
    val evolucion: Especie
  }
  case class Nivel(evolucion: Especie, nivelAEvoluicionar: Int) extends CondicionEvolutiva
  case class Intercambio(evolucion: Especie) extends CondicionEvolutiva

  trait Piedra
  case class PiedraConTipo(tipo: Tipo) extends Piedra
  case object Lunar extends Piedra

  case class CondicionPiedra(evolucion: Especie, tipoPiedra: Piedra) extends CondicionEvolutiva

//  La especie Charmander evoluciona a Charmeleon al llegar al Nivel 16, la cual a su vez,
//  evoluciona en Charizard al llegar al Nivel 36. La especie Charizard no evoluciona.
  lazy val charmander = Especie(
    Fuego, None, 10, Some(Nivel(charmeleon, 16)), Stats(1,1,1)
  )
  lazy val charmeleon = Especie(
    Fuego, None, 10, Some(Nivel(charizard, 36)), Stats(1,1,1)
  )
  lazy val charizard = Especie(
    Fuego, None, 10, None, Stats(1,1,1)
  )
}

