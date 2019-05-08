package tadp.ageofempires

class Guerrero(val potencialOfensivo:Int = 20) extends Defensor with Atacante {
  val potencialDefensivo = 10

  def recibeDanio(danio: Int) = {
    this.energia = (this.energia - danio).max(0)
  }
}