package org.uqbar.tadep.macros

object WS {
  import E02_StringInterpolation._
  import E03_Implicits._
  import E04_Macros._
  
  assert(true, "a")
  
  val c1 = new C("A", "1")
val c2 = new C("B", "2")
val c3 = new C("B", "3")

persistirConSQL(c1)(CSQL)
//	persistirConSQL(c2)(CSQL)
//	persistirConSQL(c3)(CSQL)
  
  StringContext(", ","",", ","").s(nico,ernesto,ivan)
  
  emails"$nico, $ernesto, $ivan"
  s"$nico, $ernesto, $ivan"
                           
	mail"nico@uqbar.org.ar"
                     
}