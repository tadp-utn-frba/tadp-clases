package ar.edu.utn.tadp.microprocesador

package object Operations1 {

  type Program = List[Instruction]

  // ---------------------------------------------------------------------------------------------------------------------------------
  // RESULTS
  // ---------------------------------------------------------------------------------------------------------------------------------

  object Result {
    def apply(micro: => Micro): Result = try {
      Success(micro)
    } catch {
      case error: Exception => Failure(micro, error)
    }
  }
  sealed trait Result {
    def micro: Micro
    def map(f: Micro => Micro): Result
    def flatMap(f: Micro => Result): Result
  }

  case class Success(micro: Micro) extends Result {
    def map(f: Micro => Micro) = Result(f(micro))
    def flatMap(f: Micro => Result) = f(micro)
  }

  case class Halted(micro: Micro) extends Result {
    def map(f: Micro => Micro) = this
    def flatMap(f: Micro => Result) = this
  }

  case class Failure(micro: Micro, error: Exception) extends Result {
    def map(f: Micro => Micro) = this
    def flatMap(f: Micro => Result) = this
  }

  // ---------------------------------------------------------------------------------------------------------------------------------
  // RUN: HIGHER ORDER
  // ---------------------------------------------------------------------------------------------------------------------------------

  def run(program: Program, initialMicro: Micro): Result = {
    program.foldLeft(Result(initialMicro)) { (previousResult, instruction) =>
      instruction match {
        case Add =>
          previousResult.map(micro => micro.copy(a = micro.a + micro.b))
        case Mul =>
          previousResult.map(micro => micro.copy(a = micro.a * micro.b))
        case Swap =>
          previousResult.map(micro => micro.copy(a = micro.b, b = micro.a))
        case Load(address) =>
          previousResult.map(micro => micro.copy(a = micro.mem(address)))
        case Store(address) =>
          previousResult.map(micro => micro.copy(mem = micro.mem.updated(address, micro.a)))
        case If(subInstructions) =>
          previousResult.flatMap(micro =>
            if (micro.a == 0) run(subInstructions, micro)
            else previousResult
          )
        case Halt =>
          previousResult.flatMap(micro => Halted(micro))
      }
    }
  }

}
