package ar.edu.utn.tadp.microprocesador

package object Operations3 {

  type Program = List[Instruction]

  // ---------------------------------------------------------------------------------------------------------------------------------
  // RESULTS
  // ---------------------------------------------------------------------------------------------------------------------------------

  sealed trait Result { def micro: Micro }
  object Result {
    def apply(micro: => Micro): Result = try {
      Success(micro)
    } catch {
      case error: Exception => Failure(micro, error)
    }
  }

  case class Halted(micro: Micro) extends Result
  case class Success(micro: Micro) extends Result
  case class Failure(micro: Micro, error: Exception) extends Result

  // ---------------------------------------------------------------------------------------------------------------------------------
  // RUN: AVOIDING EXCEPTIONS
  // ---------------------------------------------------------------------------------------------------------------------------------
  
  def run(program: Program, initialMicro: Micro): Result = {
		  program.foldLeft(Result(initialMicro)) {
		  case (previousResult: Halted, _)                            => previousResult
		  case (previousResult: Failure, _)                           => previousResult
		  case (Success(micro), Add)                                  => Result(micro.copy(a = micro.a + micro.b))
		  case (Success(micro), Mul)                                  => Result(micro.copy(a = micro.a * micro.b))
		  case (Success(micro), Swap)                                 => Result(micro.copy(a = micro.b, b = micro.a))
		  case (Success(micro), Load(address))                        => Result(micro.copy(a = micro.mem(address)))
		  case (Success(micro), Store(address))                       => Result(micro.copy(mem = micro.mem.updated(address, micro.a)))
		  case (previousResult @ Success(micro), If(subInstructions)) => if (micro.a == 0) run(subInstructions, micro) else previousResult
		  case (Success(micro), Halt)                                 => Halted(micro)
		  }
  }

}