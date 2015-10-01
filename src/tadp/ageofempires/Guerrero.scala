package tadp.ageofempires

class Guerrero(val potencialOfensivo:Int = 20) extends Defensor {
  val potencialDefensivo = 10

  def atacaA(otroGuerrero: Defensor) = {
    if(otroGuerrero.potencialDefensivo < this.potencialOfensivo) {
      otroGuerrero.recibeDanio(this.potencialOfensivo - otroGuerrero.potencialDefensivo)
    }
  }

  def recibeDanio(danio: Int) = {
    this.energia = (this.energia - danio).max(0)
  }
}