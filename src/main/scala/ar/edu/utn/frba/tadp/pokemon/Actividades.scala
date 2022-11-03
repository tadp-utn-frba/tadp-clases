package ar.edu.utn.frba.tadp.pokemon

trait Actividad extends (Estado => Estado)

case object Descansar extends Actividad {
  override def apply(estado: Estado): Estado = estado.map(_.energiaAlMaximo()) match {
    case Normal(_) => estado.flatmap(p => Dormido(p))
    case estado => estado
  }
}

case class Nadar(minutos: Int) extends Actividad {
  override def apply(estado: Estado): Estado = estado.pokemon match {
    case pokemon if pokemon.esDeTipo(TipoFuego) => estado.flatmap(p => KO(p))
    case pokemon if pokemon.esDeTipo(TipoAgua) => estado.map(p => efectoBase(p).ganarVelocidad(10 * minutos / 60))
    case _ => estado.map(p => efectoBase(p))
  }

  private def efectoBase(pokemon: Pokemon): Pokemon = pokemon.perderEnergia(minutos).ganarXp(20 * minutos)
}

case class LevantarPesas(peso: Int) extends Actividad {
  override def apply(estado: Estado): Estado = estado match {
    case Paralizado(_) => estado.flatmap(p => KO(p))
    case Estado(pokemon) => pokemon match {
      case p if p.esDeTipo(TipoFantasma) => estado.flatmap(p => KO(p))
      case p if peso > 10 * p.fuerza => estado.flatmap(p => Paralizado(p.perderEnergia(10)))
      case p if p.esDeTipoPrincipal(TipoPelea) => estado.flatmap(p => Normal(p.ganarXp(peso * 2)))
      case _ => estado.flatmap(p => Normal(p.ganarXp(peso)))
    }
  }
}