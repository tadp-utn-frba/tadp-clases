package tadp.ageofempires

trait Defensor {
  var energia = 100
  def potencialDefensivo:Int
  def recibeDanio(a:Int):Unit
}