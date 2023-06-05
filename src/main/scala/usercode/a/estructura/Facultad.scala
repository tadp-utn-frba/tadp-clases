package usercode.a.estructura

import stdlib.a.estructura.MyList

case class Materia(nombre: String, electiva: Boolean, cursos: MyList[Curso]) {
  override def toString: String = s"Materia($nombre, $electiva)"
}

case class Curso(codigo: Number, titular: Docente | Null, inscriptos: MyList[Alumno]) {
  override def toString: String = s"Curso($codigo)"
}

case class Alumno(apellido: String, legajo: String, regular: Boolean) {
  override def toString: String = s"Alumno($apellido, $legajo)"
}

case class Docente(apellido: String, email: String | Null) {
  override def toString: String = s"Docente($apellido, $email)"
}

