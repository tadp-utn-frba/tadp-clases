package tadp.ageofempires

class Muralla extends Defensor {
  this.energia = 1000
  val potencialDefensivo = 0
  
  def recibeDanio(danio:Int) = {
    this.energia = (this.energia - danio / 10).max(0)
  }
}