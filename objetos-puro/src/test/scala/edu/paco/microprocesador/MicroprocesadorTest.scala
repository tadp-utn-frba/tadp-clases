package edu.paco.microprocesador

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.Ignore

class MicroprocesadorTest {

  @Test
  def `lodv escribe en el acumulador A` = {
    ejecutar(
      new LodV(255),
      new Halt)

    assertRegistros(255, 0)
    assertProgramCounter(2)
  }

  @Test
  def `swap` = {
    ejecutar(
      new LodV(15),
      new Swap,
      new Halt)

    assertRegistros(0, 15)
    assertProgramCounter(3)
  }

  @Test
  def `programa que suma uno mas uno` = {
    ejecutar(
      new LodV(1),
      new Add,
      new LodV(1),
      new Add,
      new Halt //
      )

    assertRegistros(0, 2)
    assertProgramCounter(6)
  }

  @Test
  def `programa que escribe la memoria` = {
    ejecutar(
      new LodV(255),
      new Str(333),
      new Halt //
      )

    assertRegistros(255, 0)
    assertProgramCounter(5)

    assertEquals(255, micro.memoriaDatos(333))
  }

  @Test
  def `programa que lee la memoria` = {
    micro.memoriaDatos(333) = 145

    ejecutar(
      new Lod(333),
      new Halt)

    assertRegistros(145, 0)
    assertProgramCounter(3)
  }

  @Test
  def `jump incondicional` = {
    val addAt9 = new Add
    ejecutar(
      new LodV(11),
      new Add,
      new LodV(11),
      new Jmp(addAt9),
      new Swap,
      addAt9, // 9
      new LodV(7),
      new Halt)

    assertRegistros(7, 22)
  }

  @Test
  def `contador 3 con JNZ` = {
    micro.memoriaDatos(10) = 58
    val beginInstruction = new LodV(1)
    ejecutar(
      new LodV(3),
      new Str(10),

      beginInstruction,
      new Swap,
      new Lod(10),
      new Sub,
      new Swap,
      new Str(10),
      new Jnz(beginInstruction),
      new Halt)

    assertEquals(0, micro.memoriaDatos(10))
  }

  @Test
  def `contador 3 con JZ` = {
    micro.memoriaDatos(10) = 58

    val beginInstruction = new LodV(1)
    val endInstruction = new Halt
    ejecutar(
      new LodV(3),
      new Str(10),

      beginInstruction,
      new Swap,
      new Lod(10),
      new Sub,
      new Swap,
      new Str(10),
      new Jz(endInstruction),
      new Jmp(beginInstruction),
      endInstruction)

    assertEquals(0, micro.memoriaDatos(10))
  }

  @Test
  def `If que ejecuta lo que esta dentro` = {
    ejecutar(
      new LodV(3),
      new Ifnz(
        new Swap,
        new LodV(1),
        new Add),
      new LodV(5),
      new Halt)

    assertRegistros(5, 4)
    assertProgramCounter(10)
  }

  @Test
  def `If que NO ejecuta lo que esta dentro` = {
    ejecutar(
      new LodV(3),
      new Swap,
      new Ifnz(
        new LodV(5)),
      new Halt)

    assertRegistros(0, 3)
    assertProgramCounter(7)
  }

  @Test
  def `WHNZ que cuenta hasta 3` = {
    ejecutar(
      new LodV(3),
      new Whnz(
        new Str(10),
        new LodV(1),
        new Swap,
        new Lod(10),
        new Sub,
        new Swap),
      new Halt)

    assertRegistros(0, 0)
    assertProgramCounter(15)
  }

  @Test
  def `WHNZ que no entra nunca` = {
    ejecutar(
      new LodV(3),
      new Swap,
      new Whnz(
        new Nop,
        new Nop,
        new Nop,
        new Nop,
        new Nop),
      new Halt)

    assertRegistros(0, 3)
    assertProgramCounter(10)
  }

  @Test
  def `HALT en medio de un programa` = {
    ejecutar(
      new LodV(3),
      new Halt,
      new LodV(5))

    assertRegistros(3, 0)
    assertProgramCounter(2)
  }

  @Test
  def `Division por cero` = {
    ejecutar(
      new Nop,
      new Nop,
      new Div)

    assertRegistros(0, 0)
    assertProgramCounter(2)
  }

  @Test
  def `Solo WHNZ` = {
    ejecutar(
      new Whnz,
      new Halt)

    assertRegistros(0, 0)
    assertProgramCounter(2)
  }

  @Test
  def `WHNZ con HALT dentro` = {
    ejecutar(
      new LodV(1),
      new Whnz(
        new Nop,
        new Halt),
      new LodV(5))

    assertRegistros(1, 0)
    assertProgramCounter(4)
  }

  val micro = new Microprocesador

  def ejecutar(instrucciones: Instruccion*) = micro.ejecutar(new Programa(instrucciones: _*))

  def assertRegistros(valorA: Acumulador, valorB: Acumulador) = {
    assertEquals("acumulador A", valorA, micro.registros.a)
    assertEquals("acumulador B", valorB, micro.registros.b)
  }

  def assertProgramCounter(pc: Address): Unit = {
    assertEquals("program counter", pc, micro.pc)
  }

}