package ar.edu.utn.tadp.microprocesador

import org.junit.Assert._
import org.junit.Test
import ar.edu.utn.tadp.microprocesador._
import org.junit.Before
import org.junit.runner.RunWith

class MicroprocesadorTest {

  var micro: Microprocesador = _
  var resultado: ResultadoDeEjecucion = _

  // ****************************************************************
  // ** SET-UP & TEAR-DOWN
  // ****************************************************************

  @Before
  def before = micro = new Microprocesador

  // ****************************************************************
  // ** ASSERTS
  // ****************************************************************

  def assertRegistros(expectedA: Short, expectedB: Short) =
    assertOnHalt(micro => {
      assertEquals("Registro A", expectedA, micro.a)
      assertEquals("Registro B", expectedB, micro.b)
    })

  def assertProgramCounter(expectedPc: Int) =
    assertOnHalt(micro => assertEquals("Program counter", expectedPc, micro.pc))

  def assertMemoriaDatos(posicion: Int, valor: Short) = assertOnHalt(micro =>
    assertEquals(s"memoria datos at $posicion", valor, micro.memoriaDeDatos(posicion)))

  def assertOnHalt(assertion: Microprocesador => Unit) = resultado match {
    case Halt(micro) => assertion(micro)
    case _ => fail
  }

  def ejecutarEnMicro(instrucciones: Instruccion*) =
    resultado = ejecutar(micro, instrucciones: _*)
  // ****************************************************************
  // ** TESTS
  // ****************************************************************

  @Test
  def `lodv escribe en el acumulador A` = {
    ejecutarEnMicro(
      LODV(255),
      HALT)

    assertRegistros(255, 0)
    assertProgramCounter(2)
  }

  @Test
  def `swap` = {
    ejecutarEnMicro(
      LODV(15),
      SWAP,
      HALT)

    assertRegistros(0, 15)
    assertProgramCounter(3)
  }

  @Test
  def `programa que suma uno mas uno` = {
    ejecutarEnMicro(
      LODV(1),
      ADD,
      LODV(1),
      ADD,
      HALT)

    assertRegistros(0, 2)
    assertProgramCounter(6)
  }

  @Test
  def `programa que escribe la memoria` = {
    ejecutarEnMicro(
      LODV(255),
      STR(333),
      HALT)

    assertRegistros(255, 0)
    assertProgramCounter(5)
    assertMemoriaDatos(333, 255)
  }

  @Test
  def `programa que lee la memoria` = {
    micro = micro.copy(memoriaDeDatos = micro.memoriaDeDatos.updated(333.toShort, 145.toShort))

    ejecutarEnMicro(
      LOD(333),
      HALT)

    assertRegistros(145, 0)
    assertProgramCounter(3)
  }

  @Test
  def `If que ejecuta lo que esta dentro` = {
    ejecutarEnMicro(
      LODV(3),
      IFNZ(
        SWAP,
        LODV(1),
        ADD),
      LODV(5),
      HALT)

    assertRegistros(5, 4)
    assertProgramCounter(10)
  }

  @Test
  def `If que NO ejecuta lo que esta dentro` = {
    ejecutarEnMicro(
      LODV(3),
      SWAP,
      IFNZ(
        LODV(5)),
      HALT)

    assertRegistros(0, 3)
    assertProgramCounter(7)
  }

  @Test
  def `HALT en medio de un programa` = {
    ejecutarEnMicro(
      LODV(3),
      HALT,
      LODV(5))

    assertRegistros(3, 0)
    assertProgramCounter(2)
  }

}