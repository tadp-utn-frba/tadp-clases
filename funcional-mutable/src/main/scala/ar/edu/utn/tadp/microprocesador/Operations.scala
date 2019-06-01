package ar.edu.utn.tadp.microprocesador

package object Operations {

  type Program = List[Instruction]
  class ExecutionHaltException extends RuntimeException
  
  // ---------------------------------------------------------------------------------------------------------------------------------
  // RUN
  // ---------------------------------------------------------------------------------------------------------------------------------

  def run(program: Program, micro: Micro) {
    for (instruction <- program)
      instruction match {
        case Add => micro.a = micro.a + micro.b
        case Mul => micro.a = micro.a * micro.b
        case Swap =>
          val temp = micro.a
          micro.a = micro.b
          micro.b = temp
        case Load(address)       => micro.a = micro.mem(address)
        case Store(address)      => micro.mem(address) = micro.a
        case If(subInstructions) => if (micro.a == 0) run(subInstructions, micro)
        case Halt                => throw new ExecutionHaltException
      }
  }

  // ---------------------------------------------------------------------------------------------------------------------------------
  // PRINT
  // ---------------------------------------------------------------------------------------------------------------------------------

  def print(program: Program): String = program.map {
    case Add                 => "ADD"
    case Mul                 => "MUL"
    case Swap                => "SWP"
    case Load(address)       => s"LOD[$address]"
    case Store(address)      => s"STR[$address]"
    case If(subInstructions) => s"IF[${print(subInstructions)}]"
    case Halt                => "HLT"
  }.mkString(", ")

  // ---------------------------------------------------------------------------------------------------------------------------------
  // SIMPLIFY
  // ---------------------------------------------------------------------------------------------------------------------------------

  def simplify(program: Program): Program = program match {
    case Nil => Nil
    case Swap :: Swap :: otherInstructions => simplify(otherInstructions)
    case Load(_) :: (load: Load) :: otherInstructions => simplify(load :: otherInstructions)
    case (s1: Store) :: (s2: Store) :: otherInstructions if s1.address == s2.address => simplify(s2 :: otherInstructions)
    case If(subInstructions) :: otherInstructions =>
      val simplifiedSubInstructions = simplify(subInstructions)
      if (simplifiedSubInstructions.isEmpty) simplify(otherInstructions) else If(simplifiedSubInstructions) :: simplify(otherInstructions)
    case Halt :: _ => Halt :: Nil
    case i :: is   => i :: simplify(is)
  }
}
