package ar.edu.utn.tadp.microprocesador

sealed trait Instruction

case object Add extends Instruction
case object Mul extends Instruction
case object Swap extends Instruction
case class Load(address: Int) extends Instruction 
case class Store(address: Int) extends Instruction
case class If(subInstructions: List[Instruction]) extends Instruction
case object Halt extends Instruction