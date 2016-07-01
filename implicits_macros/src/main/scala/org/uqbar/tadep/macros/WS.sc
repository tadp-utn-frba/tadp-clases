package org.uqbar.tadep.macros

object WS {
  import E02_StringInterpolation._
  
  emails"$nico, $ernesto, $ivan"                  //> res0: String = nsca...@gmail.com, boss...@gmail.com, ivan...@hotmail.com.es
  s"$nico, $ernesto, $ivan"                       //> res1: String = nscarcella@gmail.com, bossi.ernestog@gmail.com, ivanlocolosre
                                                  //| doooo1964@hotmail.com.es
                           
	mail"nico@uqbar.org.ar"                   //> res2: org.uqbar.tadep.macros.E02_StringInterpolation.Email = nico@uqbar.org.
                                                  //| ar
}