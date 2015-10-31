package ar.edu.tadp.pokemon

object GimnasioPokemon {

  trait TipoPokemon
  case object Pelea extends TipoPokemon
  case object Agua extends TipoPokemon
  case object Fantasma extends TipoPokemon
  case object Fuego extends TipoPokemon
  case object Volador extends TipoPokemon

  case class Tipos(tipoPrimario: TipoPokemon,
                   tipoSecundario: Option[TipoPokemon] = None) {
    def es(tipo: TipoPokemon) = asList.contains(tipo)
    def asList = List(tipoPrimario) ++ tipoSecundario
  }

  case class Pokemon(
      energiaMaxima: Int,
      fuerza: Int,
      velocidad: Int,
      energia: Int,
      especie: Especie,
      experiencia: Int = 1) {

    require(experiencia >= 0, "La experiencia no puede ser menor a 0")

    def ganarExperiencia(cuanta: Int) =
      copy(experiencia = experiencia + cuanta)

    def perderEnergia(cuanta: Int) =
      copy(energia = energia - cuanta)

    def ganarVelocidad(cuanta: Int) = {
      copy(velocidad = velocidad + cuanta)
    }

    val tipos = especie.tipos

    lazy val nivel = {
      (1 to 100).reverse.find({
        nivel =>
          especie.experienciaNecesariaPara(nivel) < experiencia
      }).get
    }

    lazy val otroNivel = {
      def nivelR(experienciaParaNivel: Int,
                 nivel: Int): Int = {
        val experienciaParaProximoNivel = 2 * experienciaParaNivel + especie.resistenciaEvolutiva
        if (experienciaParaProximoNivel > experiencia) {
          nivel
        } else {
          nivelR(experienciaParaProximoNivel, nivel + 1)
        }
      }
      nivelR(0, 1)
    }

  }

  case class Especie(tipos: Tipos,
                     incrementoEnergiaMaxima: Int,
                     incrementoFuerza: Int,
                     incrementoVelocidad: Int,
                     resistenciaEvolutiva: Int) {
    def experienciaNecesariaPara(nivel: Int): Int = {
      if (nivel == 1) {
        0
      } else {
        resistenciaEvolutiva + 2 *
          experienciaNecesariaPara(nivel - 1)
      }
    }
  }

  type Actividad = Pokemon => Pokemon

  val descansar = (pokemon:Pokemon) => {
    pokemon.copy(
      energia = pokemon.energiaMaxima)
  }

  def levantarPesas(peso: Int): Actividad =
    (pokemon) => {
      (pokemon.tipos, pokemon.fuerza) match {
        case (tipos, _) if tipos.es(Fantasma) =>
          throw new RuntimeException("No puede levantar pesas porque es etéreo")
        case (_, fuerza) if peso > 10 * fuerza =>
          pokemon.perderEnergia(10)
        case (tipos, _) if tipos.es(Pelea) =>
          pokemon.ganarExperiencia(2 * peso)
        case _ => pokemon.ganarExperiencia(peso)
      }
    }

  def nadar(minutos: Int): Actividad =
    (pokemon) => {
      val pokemonEntrenado = pokemon
        .ganarExperiencia(200 * minutos)
        .perderEnergia(minutos)

      pokemonEntrenado.tipos match {
        case tipos if tipos.es(Agua) => pokemonEntrenado
          .ganarVelocidad(minutos / 60)
        case _ => pokemonEntrenado
      }
    }

}