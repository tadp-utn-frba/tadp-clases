package edu.paco.microprocesador

trait InstruccionVisitor {
  def visitNop(nop: Nop)
  def visitAdd(add: Add)
  def visitSub(sub: Sub)
  def visitMul(mul: Mul)
  def visitDiv(div: Div)
  def visitSwap(swap: Swap)
  def visitHalt(halt: Halt)
  def visitLodV(lodV: LodV)
  def visitLod(lod: Lod)
  def visitStr(str: Str)
  def visitJmp(jmp: Jmp)
  def visitJz(jz: Jz)
  def visitJnz(jnz: Jnz)

  def visitPrograma(programa: Programa)
  def visitIfnz(ifnz: Ifnz)
  def visitWhnz(whnz: Whnz)
}

trait Instruccion {
  def accept(visitor: InstruccionVisitor)

  def size: Int
}

abstract class InstruccionSimple(var size: Int) extends Instruccion {

}

abstract class InstruccionCompuesta(val instrucciones: Instruccion*) extends Instruccion {
  override def size = 1

  def innerSize: Int = instrucciones.map(i => i.size).sum
}

class Nop extends InstruccionSimple(1) {
  def accept(visitor: InstruccionVisitor) = visitor.visitNop(this)
}

class Add extends InstruccionSimple(1) {
  def accept(visitor: InstruccionVisitor) = visitor.visitAdd(this)
}

class Sub extends InstruccionSimple(1) {
  def accept(visitor: InstruccionVisitor) = visitor.visitSub(this)
}

class Mul extends InstruccionSimple(1) {
  def accept(visitor: InstruccionVisitor) = visitor.visitMul(this)
}

class Div extends InstruccionSimple(1) {
  def accept(visitor: InstruccionVisitor) = visitor.visitDiv(this)
}

class Swap extends InstruccionSimple(1) {
  def accept(visitor: InstruccionVisitor) = visitor.visitSwap(this)
}

class LodV(var value: Acumulador) extends InstruccionSimple(2) {
  def accept(visitor: InstruccionVisitor) = visitor.visitLodV(this)
}

class Lod(var address: Address) extends InstruccionSimple(3) {
  def accept(visitor: InstruccionVisitor) = visitor.visitLod(this)
}

class Str(var address: Address) extends InstruccionSimple(3) {
  def accept(visitor: InstruccionVisitor) = visitor.visitStr(this)
}

class Jmp(var onJumpInstruccion: Instruccion) extends InstruccionSimple(3) {
  def accept(visitor: InstruccionVisitor) = visitor.visitJmp(this)
}

class Jz(var onJumpInstruccion: Instruccion) extends InstruccionSimple(3) {
  def accept(visitor: InstruccionVisitor) = visitor.visitJz(this)

}

class Jnz(var onJumpInstruccion: Instruccion) extends InstruccionSimple(3) {
  def accept(visitor: InstruccionVisitor) = visitor.visitJnz(this)
}

class Programa(override val instrucciones: Instruccion*) extends InstruccionCompuesta(instrucciones:_*) {
  def accept(visitor: InstruccionVisitor) = visitor.visitPrograma(this)
}

class Ifnz(override val instrucciones: Instruccion*) extends InstruccionCompuesta(instrucciones:_*) {
  def accept(visitor: InstruccionVisitor) = visitor.visitIfnz(this)
}

class Whnz(override val instrucciones: Instruccion*) extends InstruccionCompuesta(instrucciones:_*) {
  def accept(visitor: InstruccionVisitor) = visitor.visitWhnz(this)
}

class Halt extends InstruccionSimple(1) {
  def accept(visitor: InstruccionVisitor) = visitor.visitHalt(this)
}