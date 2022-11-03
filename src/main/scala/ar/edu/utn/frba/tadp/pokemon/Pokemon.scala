package ar.edu.utn.frba.tadp.pokemon
import scala.math.pow

case class Pokemon(statsBase: Stats, especie: Especie, energia: Int, xp: Int = 0) {
  lazy val nivel: Int = Stream.from(1).find(nivel => especie.xpNecesariaNivel(nivel) > xp).get - 1

  lazy val statsActuales: Stats = especie.statsActuales(statsBase, nivel)

  lazy val fuerza: Int = statsActuales.fuerza
  lazy val velocidad: Int = statsActuales.velocidad
  lazy val energiaMaxima: Int = statsActuales.energiaMaxima

  def intentarEvolucionar(): Pokemon = especie.intentarEvolucionar(this)

  def esDeTipo(tipo: Tipo): Boolean = especie.esDeTipo(tipo)

  def esDeTipoPrincipal(tipo: Tipo): Boolean = especie.esDeTipo(tipo)

  def ganarVelocidad(cantidad: Int): Pokemon = copy(statsBase = statsBase.ganarVelocidad(cantidad))

  def ganarXp(cantidad: Int): Pokemon = copy(xp = xp + cantidad)

  def perderEnergia(cantidad: Int): Pokemon = copy(energia = energia - cantidad)

  def energiaAlMaximo(): Pokemon = copy(energia = statsBase.energiaMaxima)
}

case class Stats(fuerza: Int, velocidad: Int, energiaMaxima: Int) {
  def *(m: Int): Stats = copy(fuerza * m, velocidad * m, energiaMaxima * m)

  def *(m: Stats): Stats = copy(fuerza * m.fuerza, velocidad * m.velocidad, energiaMaxima * m.energiaMaxima)

  def ganarVelocidad(cantidad: Int): Stats = copy(velocidad = velocidad + cantidad)
}

case class Especie(tipoPrincipal: Tipo,
                   tipoSecundario: Option[Tipo],
                   resistenciaEvolutiva: Int,
                   multiplicadores: Stats,
                   evolucion: List[Evolucion]) {
  def intentarEvolucionar(pokemon: Pokemon): Pokemon = evolucion.flatMap(_.evolucionar(pokemon)).headOption.getOrElse(pokemon)

  def statsActuales(statsBase: Stats, nivel: Int): Stats = statsBase * multiplicadores * nivel

  def esDeTipoPrincipal(tipo: Tipo): Boolean = tipoPrincipal == tipo

  def esDeTipo(tipo: Tipo): Boolean = esDeTipoPrincipal(tipo) || tipoSecundario.contains(tipo)

  def xpNecesariaNivel(nivel: Int): Int = (pow(2, nivel).toInt - 1) * resistenciaEvolutiva
}

case class Evolucion(especie: Especie, condicion: Pokemon => Boolean) {
  def evolucionar(pokemon: Pokemon): Option[Pokemon] =
    if(condicion(pokemon)) Some(pokemon.copy(especie = especie)) else None
}

trait Tipo

case object TipoAgua extends Tipo
case object TipoFuego extends Tipo
case object TipoFantasma extends Tipo
case object TipoPelea extends Tipo