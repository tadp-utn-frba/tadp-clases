package ar.edu.utn.frba.tadp.pokemon

trait Estado {
  val pokemon: Pokemon

  def map(f: Pokemon => Pokemon): Estado

  def flatmap(f: Pokemon => Estado): Estado

  def realizarActividad(actividad: Actividad): Estado = actividad(this).map(_.intentarEvolucionar())
}

object Estado {
  def unapply(estado: Estado): Option[Pokemon] = Some(estado.pokemon)
}

case class Normal(pokemon: Pokemon) extends Estado {
  override def map(f: Pokemon => Pokemon): Estado = copy(pokemon = f(pokemon))

  override def flatmap(f: Pokemon => Estado): Estado = f(pokemon)
}

case class Dormido(pokemon: Pokemon, turnos: Int = 3) extends Estado {
  override def map(f: Pokemon => Pokemon): Estado = if (turnos > 0) this else Normal(f(pokemon))

  override def flatmap(f: Pokemon => Estado): Estado = if (turnos > 0) this else f(pokemon)
}

case class Paralizado(pokemon: Pokemon) extends Estado {
  override def map(f: Pokemon => Pokemon): Estado = Normal(pokemon.perderEnergia(10))

  override def flatmap(f: Pokemon => Estado): Estado = f(pokemon.perderEnergia(10))
}

case class KO(pokemon: Pokemon) extends Estado {
  override def map(f: Pokemon => Pokemon): Estado = this

  override def flatmap(f: Pokemon => Estado): Estado = this
}