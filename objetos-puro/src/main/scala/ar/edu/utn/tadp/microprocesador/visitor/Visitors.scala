package ar.edu.utn.tadp.microprocesador.visitor

import ar.edu.utn.tadp.microprocesador.Micro
import ar.edu.utn.tadp.microprocesador.ExecutionHaltException
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Stack

trait InstructionVisitor {
  def visitProgram(program: Program) = program.accept(this)
  def visitInstruction(instruction: Instruction) = instruction.accept(this)

  def visitAdd(instruction: Add.type): Unit
  def visitMul(instruction: Mul.type): Unit
  def visitSwap(instruction: Swap.type): Unit
  def visitLoad(instruction: Load): Unit
  def visitStore(instruction: Store): Unit
  def visitIf(instruction: If): Unit
  def endVisitIf(instruction: If): Unit
  def visitHalt(instruction: Halt.type): Unit
}

// ---------------------------------------------------------------------------------------------------------------------------------
// RUN
// ---------------------------------------------------------------------------------------------------------------------------------

class RunVisitor(micro: Micro) extends InstructionVisitor {
  private var ignoreInstructions = false
  
  override def visitInstruction(instruction: Instruction) = if(!ignoreInstructions) super.visitInstruction(instruction)
  
  def visitAdd(instruction: Add.type): Unit = {
    micro.a = micro.a + micro.b
  }

  def visitMul(instruction: Mul.type): Unit = {
    micro.a = micro.a * micro.b
  }

  def visitSwap(instruction: Swap.type): Unit = {
    val temp = micro.a
    micro.a = micro.b
    micro.b = temp
  }

  def visitLoad(instruction: Load): Unit = {
    micro.a = micro.mem(instruction.address)
  }

  def visitStore(instruction: Store): Unit = {
    micro.mem(instruction.address) = micro.a
  }

  def visitIf(instruction: If): Unit = {
    if (micro.a != 0) ignoreInstructions = true
  }
  
  def endVisitIf(instruction: If): Unit = {
    ignoreInstructions = false
  }

  def visitHalt(instruction: Halt.type): Unit = {
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
  
  def visitAdd(instruction: Add.type): Unit = {
    simplified.top += instruction

    justVisitedSwap = false
    justLoadedA = false
    justWroteMem = false
  }

  def visitMul(instruction: Mul.type): Unit = {
    simplified.top += instruction

    justVisitedSwap = false
    justLoadedA = false
    justWroteMem = false
  }

  def visitSwap(instruction: Swap.type): Unit = {
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

  def visitLoad(instruction: Load): Unit = {
    if(justLoadedA) simplified.top.dropRight(1)
    
    simplified.top += instruction
    
    justVisitedSwap = false
    justWroteMem = false
    justLoadedA = true
  }

  def visitStore(instruction: Store): Unit = {
    if(justWroteMem && lastWroteMemAddress == instruction.address) simplified.top.dropRight(1)
    
    simplified.top += instruction
    
    justVisitedSwap = false
    justWroteMem = true
    lastWroteMemAddress = instruction.address
    justLoadedA = false
  }

  def visitIf(instruction: If): Unit = {
    simplified.push(ListBuffer[Instruction]())
    
    justVisitedSwap = false
    justWroteMem = false
    justLoadedA = false
  }
  
  def endVisitIf(instruction: If): Unit = {
    val subInstructions = simplified.pop()
    if(subInstructions.nonEmpty) simplified.top += new If(new Program(subInstructions))
    
    justVisitedSwap = false
    justWroteMem = false
    justLoadedA = false
  }

  def visitHalt(instruction: Halt.type): Unit = {
    simplified.top += instruction
    
    visitedHalt = true
  }
  
  def result = new Program(simplified.top)
}