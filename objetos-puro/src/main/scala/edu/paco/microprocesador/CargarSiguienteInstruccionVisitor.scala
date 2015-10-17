package edu.paco.microprocesador

class CargarSiguienteInstruccionVisitor(var micro: Microprocesador) extends InstruccionVisitor {

  var ultimaInstruccion: Instruccion = null

  def visitPrograma(programa: Programa) = {
    programa.instrucciones.foreach(instruccion => instruccion.accept(this))
  }

  def visitNop(nop: Nop) = cargarSiguienteInstruccion(nop)

  def visitAdd(add: Add) = cargarSiguienteInstruccion(add)

  def visitSub(sub: Sub) = cargarSiguienteInstruccion(sub)

  def visitMul(mul: Mul) = cargarSiguienteInstruccion(mul)

  def visitDiv(div: Div) = cargarSiguienteInstruccion(div)

  def visitSwap(swap: Swap) = cargarSiguienteInstruccion(swap)

  def visitLodV(lodV: LodV) = cargarSiguienteInstruccion(lodV)

  def visitLod(lod: Lod) = cargarSiguienteInstruccion(lod)

  def visitStr(str: Str) = cargarSiguienteInstruccion(str)

  def visitJmp(jmp: Jmp) = cargarSiguienteInstruccion(jmp)

  def visitJz(jz: Jz) = cargarSiguienteInstruccion(jz)

  def visitJnz(jnz: Jnz) = cargarSiguienteInstruccion(jnz)

  def visitIfnz(ifnz: Ifnz) = cargarSiguienteInstruccion(ifnz)

  def visitWhnz(whnz: Whnz) = cargarSiguienteInstruccion(whnz)

  def visitHalt(halt: Halt) = cargarSiguienteInstruccion(halt)

  def cargarSiguienteInstruccion(instruccion: Instruccion): Unit = {
    //    if (ultimaInstruccion != null) {
    //      ultimaInstruccion.siguienteInstruccion = instruccion
    //    }
    //    ultimaInstruccion = instruccion
  }

}