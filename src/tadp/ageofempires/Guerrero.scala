package tadp.ageofempires

class Guerrero {
  var energia = 100
  val potencialOfensivo = 20
  val potencialDefensivo = 10

  def atacaA(otroGuerrero: Guerrero) = {
    if(otroGuerrero.potencialDefensivo < this.potencialOfensivo) {
      otroGuerrero.recibeDanio(this.potencialOfensivo - otroGuerrero.potencialDefensivo)
    }
  }

  def recibeDanio(danio: Int) = {
    this.energia-= danio
  }
}