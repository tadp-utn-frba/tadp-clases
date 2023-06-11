package ar.edu.utn.tadp.microprocesador

package object Operations2 {

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
  // RUN: FOR COMPREHENSION
  // ---------------------------------------------------------------------------------------------------------------------------------

  def run(program: Program, initialMicro: Micro): Result = {
    program.foldLeft(Result(initialMicro)) { (previousResult, instruction) =>
      instruction match {
        case Add =>
          for (micro <- previousResult)
          yield micro.copy(a = micro.a + micro.b)

        case Mul =>
          for (micro <- previousResult)
          yield micro.copy(a = micro.a * micro.b)

        case Swap =>
          for (micro <- previousResult)
          yield micro.copy(a = micro.b, b = micro.a)

        case Load(address) =>
          for (micro <- previousResult)
          yield micro.copy(a = micro.mem(address))

        case Store(address) =>
          for (micro <- previousResult)
          yield micro.copy(mem = micro.mem.updated(address, micro.a))

        case If(subInstructions) =>
          for {
            micro <- previousResult
            nextMicro <- run(subInstructions, micro)
          } yield if (micro.a == 0) nextMicro else micro

        case Halt =>
          for {
            micro <- previousResult
            haltedMicro <- Halted(micro)
          } yield haltedMicro
      }
    }
  }
}
