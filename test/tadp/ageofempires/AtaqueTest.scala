package tadp.ageofempires

import org.junit.Assert.assertEquals
import org.junit.Test

class AtaqueTest {
  
  @Test
  def unGuerreroAtacaAOtroGuerrero() {
    val g1 = new Guerrero
    val g2 = new Guerrero
    
    g1.atacaA(g2)
    
    assertEquals(90, g2.energia)
  }
  
}