package ar.edu.utn.frba.tadp.pokemon

import scala.math.pow
import scala.util.Try

sealed trait Tipo {
  def unapply(pokemon: Pokemon): Option[(Boolean, Boolean)] = {
    if (pokemon.especie.tipoPrincipal == this) {
      Some((true, false))
    } else if (pokemon.especie.tipoSecundario.contains(this)) {
      Some((false, true))
    } else {
      None
    }

    // if (pokemon.esDeTipo(this)) Some((true, false)) else None
  }
}

case object Fuego extends Tipo
case object Agua extends Tipo
case object Fantasma extends Tipo
case object Pelea extends Tipo

sealed trait Estado
case object Normal extends Estado
case class Dormido(actividadesRealizadas: Int) extends Estado
case object Paralizado extends Estado
case object KO extends Estado


case class Especie(
  tipoPrincipal: Tipo,
  tipoSecundario: Option[Tipo],
  resistenciaEvolutiva: Int,
  incremento: Stats) {
  def xpParaNivel(nivel: Int): Int = {
    if (nivel == 1)
      0
    else 2 * xpParaNivel(nivel - 1) + resistenciaEvolutiva
  }
}

case class Stats(
  energiaMax: Int,
  fuerza: Int,
  velocidad: Int
) {
  def ganarVelocidad(velocidadGanada: Int) = copy(velocidad =  velocidad + velocidadGanada)
}

case class Pokemon(
  xp: Int,
  energia: Int,
  stats: Stats,
  especie: Especie,
  estado: Estado
) {
  require(stats.fuerza >= 1 && stats.fuerza <= 100)
  require(stats.velocidad >= 1 && stats.velocidad <= 100)
  require(energia >= 0 && energia <= stats.energiaMax)

  lazy val nivel: Int =
    Stream.from(1).find(nivel => especie.xpParaNivel(nivel) > xp).getOrElse(1)

  def esDeTipo(tipo: => Tipo): Boolean =
    especie.tipoPrincipal == tipo || especie.tipoSecundario.contains(tipo)

  def ganarXp(xpGanada: Int) = copy(xp = xp + xpGanada)

  def perderEnergia(energiaPerdida: Int) = copy(energia = energia - energiaPerdida)

  def ganarVelocidad(velocidadGanada: Int) = copy(stats = stats.copy(velocidad =  stats.velocidad + velocidadGanada))
}

object actividad {
  type Tarea = Pokemon => Try[Pokemon]
  object Actividad {
    def apply(tarea: Tarea) = new Actividad {
      override def realizar(pokemon: Pokemon): Try[Pokemon] = tarea(pokemon)
    }
  }
  trait Actividad extends (Tarea) {
    protected def realizar(pokemon: Pokemon): Try[Pokemon]

    def apply(pokemonOriginal: Pokemon): Try[Pokemon] = {
      lazy val pokemonAlterado: Try[Pokemon] = realizar(pokemonOriginal)
      pokemonOriginal.estado match {
        case KO => Try {
          throw new RuntimeException("El pokemon esta KO")
        }
        case Paralizado => Try {
          pokemonOriginal
        }
        case Dormido(3) => pokemonAlterado.map(p => p.copy(estado = Normal))
        case Dormido(n) => Try {
          pokemonOriginal.copy(estado = Dormido(n + 1))
        }
        case Normal => pokemonAlterado
      }
    }

    case object Descansar extends Actividad {
      def realizar(pokemonOriginal: Pokemon): Try[Pokemon] = {
        lazy val pokemonDescansado = pokemonOriginal
          .copy(energia = pokemonOriginal.stats.energiaMax)
        Try {
          pokemonOriginal.estado match {
            case Normal => {
              if (pokemonOriginal.energia < (pokemonOriginal.stats.energiaMax / 2))
                pokemonDescansado.copy(estado = Dormido(1))
              else
                pokemonDescansado
            }
          }
        }
      }
    }

    case class LevantarPesas(kilos: Int) extends Actividad {
      def realizar(pokemon: Pokemon): Try[Pokemon] = Try {
        // tipo fantasma => no puede levantar
        // > 10kg por fuerza => no gana xp y pierde energía
        // _ => 1xp por cada kilo

        // tipo pelea => doble de puntos
        pokemon match {
          case p if p.estado == Paralizado => p.copy(estado = KO)
          case Fantasma(a, b) => pokemon
          case p if kilos / pokemon.stats.fuerza > 10 =>
            p.copy(energia = p.energia - 10, estado = Paralizado)
          case Pelea(a, b) => pokemon.copy(xp = pokemon.xp + kilos * 2)
          case _ => pokemon.copy(xp = pokemon.xp + kilos)
        }
      }
    }

    case class Nadar(minutos: Int) extends Actividad {
      def realizar(pokemon: Pokemon): Try[Pokemon] = Try {
        pokemon match {
          case Fuego(_, _) => pokemon.copy(estado = KO)
          case Agua(a, b) => efectoBase(pokemon).ganarVelocidad(10 * minutos / 60)
          case _ => efectoBase(pokemon)
        }
      }

      private def efectoBase(pokemon: Pokemon): Pokemon = pokemon.perderEnergia(minutos).ganarXp(20 * minutos)
    }
  }

  class Rutina(val nombre: String, actividades: List[Actividad]) {
    def realizar(pokemonOriginal: Pokemon): Try[Pokemon] = {
      actividades.foldLeft(Try { pokemonOriginal }) { (pokemon, actividad) =>
        pokemon.flatMap(actividad)
      }
    }
  }

  type Criterio = (Pokemon, Pokemon) => Pokemon
  def analizadorRutinas(pokemonOriginal: Pokemon,
                        rutinas: List[Rutina],
                        criterio: Criterio): Try[String] = {
    rutinas.reduce { (rutina_1, rutina_2) => {
      val tryP1 = rutina_1.realizar(pokemonOriginal)
      val tryP2 = rutina_2.realizar(pokemonOriginal)
      for {
        p1 <- tryP1
        p2 <- tryP2
      } yield (if (p1 == criterio(p1, p2)) rutina_1.nombre else rutina_2.nombre)
    }
    }
  }
}