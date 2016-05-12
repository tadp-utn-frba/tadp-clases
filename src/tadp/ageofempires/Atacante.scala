package tadp.ageofempires

trait Atacante {
  
  def potencialOfensivo: Int

  def atacaA(otroGuerrero: Defensor) = {
    if(otroGuerrero.potencialDefensivo < this.potencialOfensivo) {
      otroGuerrero.recibeDanio(this.potencialOfensivo - otroGuerrero.potencialDefensivo)
    }
  }
}
