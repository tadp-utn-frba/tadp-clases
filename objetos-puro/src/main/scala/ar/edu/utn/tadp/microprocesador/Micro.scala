package ar.edu.utn.tadp.microprocesador

class Micro {
  private val MEM_SIZE = 128
  private val REGISTER_SIZE = 1024
  
  private var _a: Int = _
  private var _b: Int = _
  private var _mem: Array[Int] = new Array(MEM_SIZE)
  
  def a = _a
  def a_=(value: Int) {
    require(value < REGISTER_SIZE && value > -REGISTER_SIZE)
    _a = value
  }
  
  def b = _b
  def b_=(value: Int) {
    require(value < REGISTER_SIZE && value > -REGISTER_SIZE)
    _b = value
  }
  
  def mem = _mem
}

class ExecutionHaltException extends RuntimeException