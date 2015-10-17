package edu.paco.microprocesador

class EjecutarProgramaVisitor(var micro: Microprocesador) extends InstruccionVisitor {

  def visitPrograma(programa: Programa) = {
    ejecutarDesde(programa, programa.instrucciones.head)
  }

  def ejecutarDesde(programa: Programa, primeraInstruccion: Instruccion): Unit = {
    try {
      val ins = programa.instrucciones.dropWhile(i => i.ne(primeraInstruccion))
      ejecutar(ins: _*)
    } catch {
      case _: StopExecution | _: DivisionByZero => // Estas son formas validas de detener el programa
      case e: ContinueOn => ejecutarDesde(programa, e.instruccion)
    }
  }

  def visitIfnz(ifnz: Ifnz) {
    micro.pc += 1
    if (micro.registros.a != 0) {
      ejecutar(ifnz.instrucciones: _*)
    } else {
      micro.pc += ifnz.innerSize
    }
  }

  def visitWhnz(whnz: Whnz) {
    micro.pc += 1
    val pcInicial = micro.pc
    while (micro.registros.a != 0) {
      micro.pc = pcInicial
      ejecutar(whnz.instrucciones: _*)
    }
    micro.pc = pcInicial + whnz.innerSize
  }

  def ejecutar(instrucciones: Instruccion*): Unit = {
    instrucciones.foreach(i => {
      i.accept(this)
      micro.pc += i.size
    })
  }

  def visitNop(nop: Nop) {}
  def visitAdd(add: Add) = micro.registros.guardar(micro.registros.a + micro.registros.b)
  def visitSub(sub: Sub) = micro.registros.guardar(micro.registros.a - micro.registros.b)
  def visitMul(mul: Mul) = micro.registros.guardar(micro.registros.a * micro.registros.b)
  def visitDiv(div: Div) = if (micro.registros.b == 0) throw new DivisionByZero else micro.registros.guardar(micro.registros.a / micro.registros.b)
  def visitSwap(swap: Swap) = micro.registros.swap
  def visitLodV(lodV: LodV) = micro.registros.a = lodV.value
  def visitLod(lod: Lod) = micro.registros.a = micro.memoriaDatos(lod.address)
  def visitStr(str: Str) = micro.memoriaDatos(str.address) = micro.registros.a
  def visitHalt(halt: Halt) = throw new StopExecution

  def visitJmp(jmp: Jmp) = throw new ContinueOn(jmp.onJumpInstruccion)
  def visitJz(jz: Jz) = if (micro.registros.a == 0) throw new ContinueOn(jz.onJumpInstruccion)
  def visitJnz(jnz: Jnz) = if (micro.registros.a != 0) throw new ContinueOn(jnz.onJumpInstruccion)

}

