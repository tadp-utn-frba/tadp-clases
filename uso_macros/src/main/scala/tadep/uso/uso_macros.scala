package tadep.uso

import org.uqbar.tadep.macros.E04_Macros._

object UsoMacros extends App {
  println(getVal{val a = "Bleh"})
  
  val a = email("saraza@gmail.com")
  
  assert(1==1, "1 debería ser igual a 1")
  
  debug {
    val a = 1 + 1
    val b = 2 + 2
    println(a + b)
  }
 
}
