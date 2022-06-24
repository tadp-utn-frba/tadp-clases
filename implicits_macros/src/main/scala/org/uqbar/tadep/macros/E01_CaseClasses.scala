package org.uqbar.tadep.macros.caseClasses

class Alumno(val nota: Int)
type Curso = List[Alumno]

//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
// Con Magia
//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

package conMagia:
  // Properties de sólo lectura
  // Copy
  // Conversión a String lindo
  // Comparación / hashing
  // Constructor sin new, parcialmente aplicable
  // Unapply
  // Etc...
  case class Materia(nombre: String, ciclo: Int)(criterioDeAprobación: Alumno => Boolean):
    def aprobados(curso: Curso) = curso filter criterioDeAprobación

//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
// Sin Magia
//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

package sinMagia:

  class Materia:
    // Properties de sólo lectura
    private var _nombre: String = _
    def nombre: String = _nombre

    private var _ciclo: Int = _
    def ciclo: Int = _ciclo

    private var _criterioDeAprobación: Alumno => Boolean = _
    private def criterioDeAprobación: Alumno => Boolean = _criterioDeAprobación

		// Constructor
    private def this(nombre: String, ciclo: Int, criterioDeAprovación: Alumno => Boolean) =
      this()
      _nombre = nombre
      _ciclo = ciclo
      _criterioDeAprobación = criterioDeAprobación

    // Copy
    def copy(nombre: String = this.nombre, ciclo: Int = this.ciclo) = new Materia(nombre, ciclo, criterioDeAprobación)

    // Conversión a String lindo
    override def toString = s"${this.getClass.getSimpleName}($nombre, $ciclo)"

    // Comparación / hashing
    override def equals(other: Any) = other match
      case m: Materia => m.nombre == nombre && m.ciclo == ciclo
      case _          => false
			
    override def hashCode = ???

    def aprobados(curso: Curso) = curso filter criterioDeAprobación

  // Companion Object
  object Materia:
    // Constructor sin new, parcialmente aplicable
    def apply(nombre: String, ciclo: Int) =
      (criterioDeAprobación: Alumno => Boolean) =>
        new Materia(nombre, ciclo, criterioDeAprobación)

    // Unapply
    def unapply(materia: Materia): Option[(String, Int)] = Some((materia.nombre, materia.ciclo))
