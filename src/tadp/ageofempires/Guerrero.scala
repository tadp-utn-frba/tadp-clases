package tadp.ageofempires

class Guerrero(val potencialOfensivo:Int = 20) {
  var energia = 100
  val potencialDefensivo = 10

  def atacaA(otroGuerrero: {def potencialDefensivo:Int; def recibeDanio(a:Int):Unit}) = {
    if(otroGuerrero.potencialDefensivo < this.potencialOfensivo) {
      otroGuerrero.recibeDanio(this.potencialOfensivo - otroGuerrero.potencialDefensivo)
    }
  }

  def recibeDanio(danio: Int) = {
    this.energia = (this.energia - danio).max(0)
  }
}