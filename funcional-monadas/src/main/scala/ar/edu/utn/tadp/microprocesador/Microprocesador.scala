package ar.edu.utn.tadp.microprocesador

case class Microprocesador(memoriaDeDatos: List[Short] = 
  (1 to 1024).map(i => 0:Short).toList, a: Short = 0, b: Short = 0,
  pc: Int = 0) 
{ 
  def pc_+=(inc: Int) = copy(pc = pc + inc)

  def guardar(valor: Int) = copy(
    a = ((valor & 0xFF00) >> 4).toShort,
    b = (valor & 0x00FF).toShort
  )
}

