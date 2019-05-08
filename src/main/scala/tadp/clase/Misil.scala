package tadp.clase

class Misil(val anioFabricacion: Int) {
  def atacaA(otro: Any) = ???

  def potencialOfensivo: Int = {
    (2016 - anioFabricacion) * 10
  }
}
