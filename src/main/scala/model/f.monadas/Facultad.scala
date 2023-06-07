package model.f.monadas

class NoHayCupoException extends Exception("No hay cupo en el curso")

class NoAutorizadoException extends Exception("El alumno no está autorizado a inscribirse")

class Materia(val nombre: String, val electiva: Boolean, val cursos: List[Curso]) {
  override def toString: String = s"Materia($nombre, $electiva)"
}

class Curso(val codigo: Number, val titular: Option[Docente], var inscriptos: List[Alumno]) {
  def hayCupos: Boolean = inscriptos.length < 4

  def inscribir(alumno: Alumno): Curso = {
    if (!hayCupos) throw new NoHayCupoException
    if (!alumno.autorizado) throw new NoAutorizadoException
    inscriptos = inscriptos :+ alumno
    this
  }

  override def toString: String = s"Curso($codigo)"
}

class Alumno(val apellido: String, val legajo: String, val regular: Boolean, var autorizado: Boolean = false) {
  override def toString: String = s"Alumno($apellido, $legajo)"
}

class Docente(val apellido: String, val email: Option[String]) {
  def autorizar(alumno: Alumno, curso: Curso): Unit = {
    alumno.autorizado = true
  }

  override def toString: String = s"Docente($apellido, $email)"
}

