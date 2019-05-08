package tadp.clase

import org.junit.Assert.assertEquals
import org.junit.Test

class AtaqueTest {
  
  @Test
  def unGuerreroAtacaAOtroGuerrero() {
    val g1 = new Guerrero
    val g2 = new Guerrero
    
    //g1.atacaA(g2)
        
    assertEquals(90, g2.energia)
  }
  
  @Test
  def unGuerreroConMuchoPotencialOfensivoAtacaAOtroGuerrero() {
    val g1 = new Guerrero(200)
    val g2 = new Guerrero
    
    //g1.atacaA(g2)
        
    assertEquals(0, g2.energia)
  }
  
  @Test
  def unGuerreroAtacaAUnaMuralla() {
    val g1 = new Guerrero
    val m = new Muralla
    
    //g1.atacaA(m)
    
    assertEquals(998, m.energia)
  }
  
  @Test
  def unMisilAtacaAUnaMuralla() {
    val misil = new Misil(1966)
    val muralla  = new Muralla
    
    //misil.atacaA(muralla)
    
    assertEquals(950, muralla.energia)
  }
  
}






