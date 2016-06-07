package ar.edu.utn.tadp.microprocesador

object for_comprehension {
  val caja: List[Int] = List(1, 2, 42)

  val tx1: Int => List[Int] = ???
  val tx2: Int => List[Int] = ???
  val crit: Int => Boolean = {_ > 3}
  val tx3: Int => Int = unInt => unInt + 4

  caja.flatMap { e1 =>
    tx1(e1).flatMap { e2 =>
      tx2(e2).filter { e3 => crit(e3) }.map { e4 => tx3(e4) }
    }
  }
  
  for {
  	e1 <- caja
  	e2 <- tx1(e1)
 		e3 <- tx2(e2)
 		if crit(e3)  	
 } yield tx3(e3)
}