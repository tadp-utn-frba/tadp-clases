package edu.paco.microprocesador

class Microprocesador {
  var registros = new Registros(0, 0)
  var memoriaDatos = new Array[Address](1024)
  var pc = 0

  def ejecutar(programa: Programa) = {
    programa.accept(new CargarSiguienteInstruccionVisitor(this))
    programa.accept(new EjecutarProgramaVisitor(this))
  }

}

class Memoria(var tamanio: Short) {
  var posiciones = new Array[Acumulador](tamanio)

  def byteAt(address: Address) = {
    posiciones(address)
  }

  def write(address: Address, value: Acumulador) = {
    posiciones(address) = value
  }

}

class Registros(
  var a: Acumulador,
  var b: Acumulador) {

  def guardar(suma: Int): Unit = {
    a = ((suma & 0xFF00) >> 4).toShort
    b = (suma & 0x00FF).toShort
  }

  def swap = {
    val temp = a
    a = b
    b = temp
  }

}