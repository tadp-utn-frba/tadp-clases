package ar.edu.utn.frba.tadp.pokemon

import scala.math.pow

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
)

case class Pokemon(
  xp: Int,
  energia: Int,
  stats: Stats,
  especie: Especie
) {
  require(stats.fuerza >= 1 && stats.fuerza <= 100)
  require(stats.velocidad >= 1 && stats.velocidad <= 100)
  require(energia >= 0 && energia <= stats.energiaMax)

  lazy val nivel: Int =
    Stream.from(1).find(nivel => especie.xpParaNivel(nivel) < xp).getOrElse(1)

  def esDeTipo(tipo: => Tipo): Boolean =
    especie.tipoPrincipal == tipo || especie.tipoSecundario.contains(tipo)
  
}


object actividad {
  trait Actividad extends (Pokemon => Pokemon)

  case object Descansar extends Actividad {
    override def apply(pokemon: Pokemon): Pokemon =
      pokemon.copy(energia = pokemon.stats.energiaMax)
  }

  case class LevantarPesas(kilos: Int) extends Actividad {
    override def apply(pokemon: Pokemon): Pokemon = {
      // tipo fantasma => no puede levantar
      // > 10kg por fuerza => no gana xp y pierde energía
      // _ => 1xp por cada kilo

      // tipo pelea => doble de puntos
      pokemon match {
        case Fantasma(a, b) => pokemon
        case p if kilos / pokemon.stats.fuerza > 10 =>
          p.copy(energia = p.energia - 10)
        case Pelea(a, b) => pokemon.copy(xp = pokemon.xp + kilos * 2)
        case _ => pokemon.copy(xp = pokemon.xp + kilos)
      }
    }
  }

  case class Nadar(minutos: Int) extends Actividad {
    override def apply(pokemon: Pokemon): Pokemon = ???
  }

  //  def descansar(pokemon: Pokemon): Pokemon = ???
  //  def levantarPesas(pokemon: Pokemon): Pokemon = ???
  //  def nadar(minutos: Int)(pokemon: Pokemon): Pokemon = ???

  //  val rutina: List[Actividad] = List(
  //    descansar, // def descansar
  //    _.descansar, // método descansar en Pokemon
  //    _.levantarPesas, // método levantarPesas en Pokemon
  //    Descansar, // object Descansar que tiene apply
  //    nadar(10)(_),
  //  )
}