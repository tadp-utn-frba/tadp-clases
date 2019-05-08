package tadp.ageofempires

class Misil(val anioFabricacion: Int) extends Atacante {

  def potencialOfensivo: Int = {
    (2016 - anioFabricacion) * 10
  }
}