package tadp.ageofempires

class Muralla {
  val potencialDefensivo = 0
  var energia = 1000
  
  def recibeDanio(danio:Int) = {
    this.energia = (this.energia - danio / 10).max(0)
  }
}