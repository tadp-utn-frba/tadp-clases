package org.uqbar.tadep.macros

object E03_Implicits {
	
	//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
	// Implicit Class
	//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
	
  class StringExtendido(unString: String) {
  	// Este método es demasiado específico para querer ponerlo en String
  	def esUnMail = unString.length > 10 && unString.contains("@") && unString.endsWith(".com") 
  }
  
	new StringExtendido("foobar@gmail.com").esUnMail // Sí!
  new StringExtendido("Hola Mundo!").esUnMail // No!
//  "foobar@gmail.com".esUnMail // Esto no anda...
  
  
  //═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
  // Implicit Conversions
  //═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

  //TODO
  
}