package ar.edu.utn.tadp.microprocesador.visitor

import ar.edu.utn.tadp.microprocesador.Micro
import ar.edu.utn.tadp.microprocesador.ExecutionHaltException
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Stack

trait InstructionVisitor {
  def visitProgram(program: Program) = program.accept(this)
  def visitInstruction(instruction: Instruction) = instruction.accept(this)

  def visitAdd(instruction: Add.type)
  def visitMul(instruction: Mul.type)
  def visitSwap(instruction: Swap.type)
  def visitLoad(instruction: Load)
  def visitStore(instruction: Store)
  def visitIf(instruction: If)
  def endVisitIf(instruction: If)
  def visitHalt(instruction: Halt.type)
}

// ---------------------------------------------------------------------------------------------------------------------------------
// RUN
// ---------------------------------------------------------------------------------------------------------------------------------

class RunVisitor(micro: Micro) extends InstructionVisitor {
  private var ignoreInstructions = false
  
  override def visitInstruction(instruction: Instruction) = if(!ignoreInstructions) super.visitInstruction(instruction)
  
  def visitAdd(instruction: Add.type) {
    micro.a = micro.a + micro.b
  }

  def visitMul(instruction: Mul.type) {
    micro.a = micro.a * micro.b
  }

  def visitSwap(instruction: Swap.type) {
    val temp = micro.a
    micro.a = micro.b
    micro.b = temp
  }

  def visitLoad(instruction: Load) {
    micro.a = micro.mem(instruction.address)
  }

  def visitStore(instruction: Store) {
    micro.mem(instruction.address) = micro.a
  }

  def visitIf(instruction: If) {
    if (micro.a != 0) ignoreInstructions = true
  }
  
  def endVisitIf(instruction: If) {
    ignoreInstructions = false
  }

  def visitHalt(instruction: Halt.type) {
    throw new ExecutionHaltException
  }
}

// ---------------------------------------------------------------------------------------------------------------------------------
// PRINT
// ---------------------------------------------------------------------------------------------------------------------------------

class PrintVisitor(micro: Micro) extends InstructionVisitor {
  private var text = ""
  
  private def addText(s: String) = text += (if(text.isEmpty) "" else ", ") + s
  
  def visitAdd(instruction: Add.type) = addText("ADD")

  def visitMul(instruction: Mul.type) = addText("MUL")

  def visitSwap(instruction: Swap.type) = addText("SWP")

  def visitLoad(instruction: Load) = addText(s"LOD[${instruction.address}]")

  def visitStore(instruction: Store) = addText(s"STR[${instruction.address}]")

  def visitIf(instruction: If) = addText("IF[")
  
  def endVisitIf(instruction: If) = text += "]"

  def visitHalt(instruction: Halt.type)  = addText("HLT")
  
  def result = text
}


// ---------------------------------------------------------------------------------------------------------------------------------
// SIMPLIFY
// ---------------------------------------------------------------------------------------------------------------------------------

class SimplifyVisitor() extends InstructionVisitor {
  private var simplified = Stack(ListBuffer[Instruction]())
  private var justVisitedSwap = false
  private var justLoadedA = false
  private var justWroteMem = false
  private var lastWroteMemAddress = 0
  private var visitedHalt = false
  
  def visitAdd(instruction: Add.type) {
	  simplified.top += instruction

	  justVisitedSwap = false
    justLoadedA = false
    justWroteMem = false
  }

  def visitMul(instruction: Mul.type) {
	  simplified.top += instruction

	  justVisitedSwap = false
    justLoadedA = false
    justWroteMem = false
  }

  def visitSwap(instruction: Swap.type) {
    if(justVisitedSwap){
      simplified.top.dropRight(1)
      justVisitedSwap = false
    } else {
      simplified.top += instruction
    	justVisitedSwap = true
    }

    justLoadedA = false
    justWroteMem = false
  }

  def visitLoad(instruction: Load) {
    if(justLoadedA) simplified.top.dropRight(1)
    
    simplified.top += instruction
    
    justVisitedSwap = false
    justWroteMem = false
    justLoadedA = true
  }

  def visitStore(instruction: Store) {
    if(justWroteMem && lastWroteMemAddress == instruction.address) simplified.top.dropRight(1)
    
    simplified.top += instruction
    
    justVisitedSwap = false
    justWroteMem = true
    lastWroteMemAddress = instruction.address
    justLoadedA = false
  }

  def visitIf(instruction: If) {
    simplified.push(ListBuffer[Instruction]())
    
    justVisitedSwap = false
    justWroteMem = false
    justLoadedA = false
  }
  
  def endVisitIf(instruction: If) {
    val subInstructions = simplified.pop()
    if(subInstructions.nonEmpty) simplified.top += new If(new Program(subInstructions))
    
    justVisitedSwap = false
    justWroteMem = false
    justLoadedA = false
  }

  def visitHalt(instruction: Halt.type) {
    simplified.top += instruction
    
    visitedHalt = true
  }
  
  def result = new Program(simplified.top)
}