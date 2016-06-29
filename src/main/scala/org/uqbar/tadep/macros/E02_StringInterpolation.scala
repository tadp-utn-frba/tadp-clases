package org.uqbar.tadep.macros

object E02_StringInterpolation {

	val nombre = "Tecnicas Avanzadas de Programación"
	val ciclo = 3
	val alumnos: List[Int] = List(8, 9, 10, 10, 6, 4, 2, 2, 8)

	//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
	// Interpolación
	//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

	val sinMagia = "La materia " + nombre + " del ciclo " + ciclo + " tiene " + alumnos.size + "alumnos"

	val conMagia = s"La materia $nombre del ciclo $ciclo tiene ${alumnos.size} alumnos"

	//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
	// Formateado
	//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

	val formateado = f"El promedio en $nombre%s es ${alumnos.sum / alumnos.size}%2.2f" // <- Type safe!
	//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
	// Sin Procesar
	//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

	val procesado = s"Estos\nSon\nSaltos\nDe\nLinea"
	val sinProcesar = raw"Estos\nNo\nSon\nSaltos\nDe\nLinea"

	//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
	// Custom
	//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

	class Email(val id: String, val dominio: String) {
		override def toString = s"$id@$dominio"
		def toEncriptedString = s"${id.take(4)}...@$dominio"
	}

	implicit class EmailsContext(val context: StringContext) {
		def emails(arguments: Any*): String =
			("" /: context.parts.zip(arguments)) {
				case (acum, (part, exp: Email)) => acum + part + exp.toEncriptedString
				case (acum, (part, exp)) => acum + part + exp
			}
	}

	//─────────────────────────────────────────────────────────────────────────────────────────────────────────────────

	val nico = new Email("nscarcella", "gmail.com")
	val ernesto = new Email("bossi.ernestog", "gmail.com")
	val ivan = new Email("ivanlocolosredoooo1964", "hotmail.com.es")

	emails"$nico, $ernesto, $ivan"
	s"$nico, $ernesto, $ivan"

	
	//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
	// Custom (no string)
	//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
		
	implicit class EmailContext(val context: StringContext) {
		def mail(arguments: Any*): Email = {
			val mergeado = context.s(arguments:_*)
			val extractor = "(.*)@(.*)".r
			val extractor(id,dominio) = mergeado
			
			new Email(id,dominio)			
		}
	}
	
}