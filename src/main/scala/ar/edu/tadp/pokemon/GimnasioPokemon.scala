package ar.edu.tadp.pokemon

object GimnasioPokemon {

  abstract class TipoPokemon(val debilidades:List[TipoPokemon] = List()) {
    def esDebilContra(tipo: TipoPokemon) = 
      debilidades.contains(tipo)
  }
  case object Pelea extends TipoPokemon(List(Volador))
  case object Agua extends TipoPokemon
  case object Fantasma extends TipoPokemon
  case object Fuego extends TipoPokemon(List(Agua))
  case object Roca extends TipoPokemon(List(Agua))
  case object Tierra extends TipoPokemon(List(Agua))
  case object Volador extends TipoPokemon

  case class Tipos(tipoPrimario: TipoPokemon,
                   tipoSecundario: Option[TipoPokemon] = None) {
    def es(tipo: TipoPokemon) = asList.contains(tipo)
    def asList = List(tipoPrimario) ++ tipoSecundario
    def esDebilContra(tipo: TipoPokemon) = {
      asList.exists { x => x.esDebilContra(tipo) }
    }
  }

  trait EstadoPokemon {
    def realizarActividad(pokemon: Pokemon, actividad: Actividad) = {
      actividad(pokemon)
    }
  }

  case object KO extends EstadoPokemon {
    override def realizarActividad(pokemon: Pokemon, actividad: Actividad) = {
      throw new RuntimeException("No puede porque está Knock Out, lo vas a matar")
    }
  }
  
  case object Paralizado extends EstadoPokemon
  case object Bueno extends EstadoPokemon
  
  case class Dormido(turnos: Int = 3) extends EstadoPokemon {
    require(turnos > 0, "turnos no puede ser negativo")
    override def realizarActividad(pokemon: Pokemon, actividad: Actividad) = {
      if(turnos == 1) {
        pokemon.cambiarEstado(Bueno)
      } else {
        pokemon.cambiarEstado(Dormido(turnos - 1))
      }
    }
  }

  case class Pokemon(
      energiaMaxima: Int,
      fuerza: Int,
      velocidad: Int,
      energia: Int,
      especie: Especie,
      estado: EstadoPokemon = Bueno,
      experiencia: Int = 1) {

    require(experiencia >= 0, "La experiencia no puede ser menor a 0")

    def ganarExperiencia(cuanta: Int) =
      copy(experiencia = experiencia + cuanta)

    def perderEnergia(cuanta: Int) =
      copy(energia = energia - cuanta)

    def ganarVelocidad(cuanta: Int) = {
      copy(velocidad = velocidad + cuanta)
    }
    
    def cambiarEstado(estadoNuevo: EstadoPokemon) =
      copy(estado = estadoNuevo)

    val tipos = especie.tipos

    lazy val nivel = {
      (1 to 100).reverse.find({
        nivel =>
          especie.experienciaNecesariaPara(nivel) < experiencia
      }).get
    }

    lazy val otroNivel = {
      //experiencia para llegar al nivel actual
      //nivel actual
      def nivelR(experienciaParaNivel: Int,
                 nivel: Int): Int = {
        val experienciaParaProximoNivel =
          2 * experienciaParaNivel + especie.resistenciaEvolutiva
        if (experienciaParaProximoNivel > experiencia) {
          nivel
        } else {
          nivelR(experienciaParaProximoNivel, nivel + 1)
        }
      }
      nivelR(0, 1)
    }

    def realizarActividad(actividad: Actividad) = {
      estado.realizarActividad(this, actividad)
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

  type Actividad = Function1[Pokemon, Pokemon]

  val descansar = (pokemon: Pokemon) => {
    val pokemonDescansado = pokemon.copy(
      energia = pokemon.energiaMaxima)
      
    if (pokemonDescansado.estado == Bueno && 
        pokemon.energia < pokemon.energiaMaxima * 0.5){
        pokemonDescansado.cambiarEstado(Dormido())
    }
    else {
      pokemonDescansado
    }
  }

  case class LevantarPesas(peso: Int) extends Actividad {
    def apply(pokemon: Pokemon) = {
      (pokemon.tipos, pokemon.fuerza, pokemon.estado) match {
        case (tipos, _, _) if tipos.es(Fantasma) =>
          throw new RuntimeException("No puede levantar pesas porque es etéreo")
        case (tipos, _, Paralizado) => pokemon.cambiarEstado(KO)  
        case (_, fuerza, _) if peso > 10 * fuerza =>
          pokemon.perderEnergia(10)
          pokemon.cambiarEstado(Paralizado)
        case (tipos, _, _) if tipos.es(Pelea) =>
          pokemon.ganarExperiencia(2 * peso)
        case _ => pokemon.ganarExperiencia(peso)
      }
    }
  }
    

  case class Nadar(minutos: Int) extends Actividad {
    def apply(pokemon:Pokemon) = {
      val pokemonEntrenado = pokemon
        .ganarExperiencia(200 * minutos)
        .perderEnergia(minutos)

      pokemonEntrenado.tipos match {
        case tipos if tipos.es(Agua) => pokemonEntrenado
          .ganarVelocidad(minutos / 60)
        case tipos if tipos.esDebilContra(Agua) => 
          pokemon.cambiarEstado(KO)  
        case _ => pokemonEntrenado
      }
    }
  }
}




