package tadp.ageofempires

abstract class Defensor {
  var energia = 100
  def potencialDefensivo:Int
  def recibeDanio(a:Int):Unit
}