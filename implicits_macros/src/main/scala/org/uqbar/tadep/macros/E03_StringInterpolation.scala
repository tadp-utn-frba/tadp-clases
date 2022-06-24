package org.uqbar.tadep.macros.stringInterpolation

val nombre  = "Tecnicas Avanzadas de Programación"
val ciclo   = 3
val alumnos = List(8, 9, 10, 10, 6, 4, 2, 2, 8)

// ═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
// Interpolación
// ═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

val sinMagia = "La materia " + nombre + " del ciclo " + ciclo + " tiene " + alumnos.size + "alumnos"

val conMagia = s"La materia $nombre del ciclo $ciclo tiene ${alumnos.size} alumnos"

// ═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
// Formateado
// ═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

val formateado = f"El promedio en $nombre%s es ${alumnos.sum / alumnos.size.toDouble}%2.2f" // <- Type safe!

// ═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
// Sin Procesar
// ═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

val procesado = s"Estos\nSon\nSaltos\nDe\nLinea"

val sinProcesar = raw"Estos\nNo\nSon\nSaltos\nDe\nLinea"

// ═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
// Custom
// ═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

class Email(val id: String, val dominio: String):
  override def toString = s"$id@$dominio"
  def toEncriptedString = s"${id.take(4)}...@$dominio"

extension (context: StringContext)
  def emailsOfuscados(arguments: Any*): String =
    val ofuscados = arguments.map {
      case mail: Email => mail.toEncriptedString
      case arg         => arg
    }

    context.s(ofuscados*)

// ═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
// Custom (no string)
// ═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

extension (context: StringContext)
  def mail(arguments: Any*): Email =
    val extractor              = "(.*)@(.*)".r
    val extractor(id, dominio) = context.s(arguments*)

    new Email(id, dominio)
