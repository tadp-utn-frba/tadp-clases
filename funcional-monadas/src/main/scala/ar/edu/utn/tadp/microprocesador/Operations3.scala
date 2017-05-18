package ar.edu.utn.tadp.microprocesador

import scala.util.Try

package object Operations3 {

  type Program = List[Instruction]
  class ExecutionHaltException(val micro: Micro) extends RuntimeException
  
  // ---------------------------------------------------------------------------------------------------------------------------------
  // RUN: TRY MONAD
  // ---------------------------------------------------------------------------------------------------------------------------------

  def run(program: Program, initialMicro: Micro): Try[Micro] = {
    program.foldLeft(Try(initialMicro)) { (previousResult, instruction) =>
      instruction match {
        case Add            => for(micro <- previousResult) yield micro.copy(a = micro.a + micro.b)
        case Mul            => for(micro <- previousResult) yield micro.copy(a = micro.a * micro.b)
        case Swap           => for(micro <- previousResult) yield micro.copy(a = micro.b, b = micro.a)
        case Load(address)  => for(micro <- previousResult) yield micro.copy(a = micro.mem(address))
        case Store(address) => for(micro <- previousResult) yield micro.copy(mem = micro.mem.updated(address, micro.a))
        
        case If(subInstructions) =>
          for {
            micro <- previousResult
            nextMicro <- run(subInstructions, micro)
          } yield if (micro.a == 0) nextMicro else micro
          
        case Halt => for {
            micro <- previousResult
            haltedMicro <- Try(throw new ExecutionHaltException(micro))
          } yield haltedMicro
      }
    }
  }

}