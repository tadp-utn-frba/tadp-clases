package edu.paco.microprocesador

class PrettyPrintVisitor extends InstruccionVisitor {

  var programString = ""
  var indents = 0

  def visitPrograma(programa: Programa) = visitInstruccionCompuesta(programa, "Programa")
  def visitIfnz(ifnz: Ifnz) = visitInstruccionCompuesta(ifnz, "IFNZ")
  def visitWhnz(whnz: Whnz) = visitInstruccionCompuesta(whnz, "WHNZ")
  def visitNop(nop: Nop) = appendString("NOP")
  def visitAdd(add: Add) = appendString("ADD")
  def visitSub(sub: Sub) = appendString("SUB")
  def visitMul(mul: Mul) = appendString("MUL")
  def visitDiv(div: Div) = appendString("DIV")
  def visitSwap(swap: Swap) = appendString("SWAP")
  def visitLodV(lodV: LodV) = appendString("LODV " + lodV.value)
  def visitLod(lod: Lod) = appendString("LOD " + lod.address)
  def visitStr(str: Str) = appendString("STR " + str.address.formatted("0x%X"))
  def visitJmp(jmp: Jmp) = appendString("JMP")
  def visitJz(jz: Jz) = appendString("JZ")
  def visitJnz(jnz: Jnz) = appendString("JNZ ")
  def visitHalt(halt: Halt) = appendString("HALT")

  def visitInstruccionCompuesta(compuesta: InstruccionCompuesta, nombreOperacion: String): Unit = {
    appendString(nombreOperacion)
    indents += 1
    visitar(compuesta.instrucciones: _*)
    indents -= 1
    appendString("END")
  }

  def visitar(instrucciones: Instruccion*): Unit = {
    instrucciones.foreach(i => i.accept(this))
  }

  def appendString(nombreOperacion: String): Unit = {
    programString += "\t" * indents
    programString += nombreOperacion
    programString += "\n"
  }

}

