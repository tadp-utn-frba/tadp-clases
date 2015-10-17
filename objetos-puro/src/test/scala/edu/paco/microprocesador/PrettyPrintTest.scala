package edu.paco.microprocesador

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.Ignore

class PrettyPrintTest {

  @Test
  def `pretty print` = {
    val visitor = new PrettyPrintVisitor
    val programa = new Programa(
      new Nop,
      new LodV(2),
      new Swap,
      new Whnz(
        new Mul,
        new Ifnz(
          new Add,
          new Sub,
          new Halt)),
      new Str(255)
      )
    programa.accept(visitor)

    assertEquals(
      """Programa
    	|	NOP
    	|	LODV 2
    	|	SWAP
    	|	WHNZ
    	|		MUL
    	|		IFNZ
    	|			ADD
    	|			SUB
    	|			HALT
    	|		END
    	|	END
    	|	STR 0xFF
    	|END
        |""".stripMargin, visitor.programString)
  }

}