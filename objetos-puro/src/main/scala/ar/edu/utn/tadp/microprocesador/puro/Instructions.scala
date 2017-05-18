package ar.edu.utn.tadp.microprocesador.puro

import ar.edu.utn.tadp.microprocesador.Micro
import ar.edu.utn.tadp.microprocesador.ExecutionHaltException

// ---------------------------------------------------------------------------------------------------------------------------------
// PROGRAM
// ---------------------------------------------------------------------------------------------------------------------------------

class Program(private var instructions: Seq[Instruction]) {
  def runOn(micro: Micro) {
    for (instruction <- instructions)
      instruction.beExecutedBy(micro)
  }

  def print = instructions.map(_.print).mkString(", ")

  def simplify {
    var simplified = 0
    while (simplified < instructions.size - 1) {
      val simplifiedPair: Seq[Instruction] = instructions(simplified).simplifiedBefore(instructions(simplified + 1))
      instructions = simplifiedPair ++ instructions.drop(2)
      simplified += 0.max(simplifiedPair.size - 1)
    }
  }

  def instructionCount = instructions.size
}

// ---------------------------------------------------------------------------------------------------------------------------------
// INSTRUCTIONS
// ---------------------------------------------------------------------------------------------------------------------------------

trait Instruction {
  def beExecutedBy(micro: Micro)

  def print: String

  def simplifiedBefore(next: Instruction): Seq[Instruction] = Seq(this, next)
  def cancelsWithSwap = false
  def overwritesAWithoutReading = false
  def overwritesAddress(address: Int) = false
}

object Add extends Instruction {
  def beExecutedBy(micro: Micro) {
    micro.a = micro.a + micro.b
  }

  def print = "ADD"
}

object Mul extends Instruction {
  def beExecutedBy(micro: Micro) {
    micro.a = micro.a * micro.b
  }

  def print = "MUL"
}

object Swap extends Instruction {
  def beExecutedBy(micro: Micro) {
    val temp = micro.a
    micro.a = micro.b
    micro.b = temp
  }

  def print = "SWP"

  override def simplifiedBefore(next: Instruction) = if (next.cancelsWithSwap) Seq() else super.simplifiedBefore(next)
  override def cancelsWithSwap = true
}

class Load(val address: Int) extends Instruction {
  def beExecutedBy(micro: Micro) {
    micro.a = micro.mem(address)
  }

  def print = s"LOD[$address]"

  override def simplifiedBefore(next: Instruction) = if (next.overwritesAWithoutReading) Seq(next) else super.simplifiedBefore(next)
  override def overwritesAWithoutReading = true
}

class Store(val address: Int) extends Instruction {
  def beExecutedBy(micro: Micro) {
    micro.mem(address) = micro.a
  }

  def print = s"STR[$address]"

  override def simplifiedBefore(next: Instruction) = if (next.overwritesAddress(address)) Seq(next) else super.simplifiedBefore(next)
  override def overwritesAddress(address: Int) = this.address == address
}

class If(subInstructions: Program) extends Instruction {
  def beExecutedBy(micro: Micro) {
    if (micro.a == 0) subInstructions.runOn(micro)
  }

  def print = s"IFZ[${subInstructions.print}]"

  override def simplifiedBefore(next: Instruction) = {
    subInstructions.simplify
    if (subInstructions.instructionCount == 0) Seq(next) else super.simplifiedBefore(next)
  }
}

object Halt extends Instruction {
  def beExecutedBy(micro: Micro) {
    throw new ExecutionHaltException
  }

  def print = "HLT"

  override def simplifiedBefore(next: Instruction) = Seq(this)
}