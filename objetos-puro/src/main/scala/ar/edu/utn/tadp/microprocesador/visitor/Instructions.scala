package ar.edu.utn.tadp.microprocesador.visitor

import ar.edu.utn.tadp.microprocesador.Micro

// ---------------------------------------------------------------------------------------------------------------------------------
// PROGRAM
// ---------------------------------------------------------------------------------------------------------------------------------

class Program(private var instructions: collection.Seq[Instruction]) {
  def instructionCount = instructions.size
  
  def accept(visitor: InstructionVisitor): Unit = {
    for (instruction <- instructions)
      instruction.accept(visitor)
  }
}

// ---------------------------------------------------------------------------------------------------------------------------------
// INSTRUCTIONS
// ---------------------------------------------------------------------------------------------------------------------------------

trait Instruction {
  def accept(visitor: InstructionVisitor): Unit
}

object Add extends Instruction {
  def accept(visitor: InstructionVisitor) = visitor.visitAdd(this)
}

object Mul extends Instruction {
  def accept(visitor: InstructionVisitor) = visitor.visitMul(this)
}

object Swap extends Instruction {
  def accept(visitor: InstructionVisitor) = visitor.visitSwap(this)
}

class Load(val address: Int) extends Instruction {
  def accept(visitor: InstructionVisitor) = visitor.visitLoad(this)
}

class Store(val address: Int) extends Instruction {
  def accept(visitor: InstructionVisitor) = visitor.visitStore(this)
}

class If(subInstructions: Program) extends Instruction {
  def accept(visitor: InstructionVisitor): Unit = {
    visitor.visitIf(this)
    subInstructions.accept(visitor)
    visitor.endVisitIf(this)
  }
}

object Halt extends Instruction {
  def accept(visitor: InstructionVisitor) = visitor.visitHalt(this)
}