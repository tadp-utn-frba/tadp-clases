package ar.edu.utn.tadp.microprocesador

package object Operations2 {

  type Program = List[Instruction]
  class ExecutionHaltException extends RuntimeException

  // ---------------------------------------------------------------------------------------------------------------------------------
  // RUN: FOLDING
  // ---------------------------------------------------------------------------------------------------------------------------------
  
  def run(program: Program, initialMicro: Micro): Micro =
    program.foldLeft(initialMicro) { (micro, instruction) =>
      instruction match {
        case Add                 => micro.copy(a = micro.a + micro.b)
        case Mul                 => micro.copy(a = micro.a * micro.b)
        case Swap                => micro.copy(a = micro.b, b = micro.a)
        case Load(address)       => micro.copy(a = micro.mem(address))
        case Store(address)      => micro.copy(mem = micro.mem.updated(address, micro.a))
        case If(subInstructions) => if (micro.a == 0) run(subInstructions, micro) else micro
        case Halt                => throw new ExecutionHaltException
      }
    }

}