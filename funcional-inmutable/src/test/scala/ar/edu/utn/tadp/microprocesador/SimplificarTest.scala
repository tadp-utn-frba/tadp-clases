package ar.edu.utn.tadp.microprocesador

import org.junit.Assert.assertEquals
import org.junit.Test
import ar.edu.utn.tadp.microprocesador._
import org.junit.Before
import org.junit.runner.RunWith

class SimplificarTest {

  @Test
  def `los NOP se eliminan` = {
    val programa = simplificar(
      NOP,
      SWAP,
      NOP,
      NOP,
      HALT,
      NOP)

    assertEquals(List(SWAP, HALT), programa)
  }

  @Test
  def `dos swap seguidos no tienen sentido` = {
    val programa = simplificar(
      SWAP,
      SWAP,
      SWAP,
      HALT)

    assertEquals(List(SWAP, HALT), programa)
  }

  @Test
  def `dos lodv seguidos se deja el ultimo` = {
    val programa = simplificar(
      LODV(3),
      LODV(2),
      HALT)

    assertEquals(List(LODV(2), HALT), programa)
  }

  @Test
  def `dos lod seguidos se deja el ultimo` = {
    val programa = simplificar(
      LOD(13),
      LOD(12),
      HALT)

    assertEquals(List(LOD(12), HALT), programa)
  }

  @Test
  def `dos str seguidos se deja el ultimo` = {
    val programa = simplificar(
      STR(13),
      STR(12),
      HALT)

    assertEquals(List(STR(12), HALT), programa)
  }

  @Test
  def `un if vacio se elimina` = {
    val programa = simplificar(
      SWAP,
      IFNZ(),
      HALT)

    assertEquals(List(SWAP, HALT), programa)
  }

  @Test
  def `se reduce dentro de un if` = {
    val programa = simplificar(
      SWAP,
      IFNZ(
        IFNZ(),
        LODV(5),
        LODV(8),
        SWAP),
      SWAP,
      SWAP,
      HALT)

    assertEquals(List(SWAP, IFNZ(LODV(8), SWAP), HALT), programa)
  }
}