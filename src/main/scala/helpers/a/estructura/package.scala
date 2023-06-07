package helpers.a

import model.abcd.funcional.{Materia, Curso, Alumno}

package object estructura {
  def electivas(materias: List[Materia]): List[Materia] = materias match {
    case Nil => Nil
    case materia::otras if materia.electiva => materia::electivas(otras)
    case _::otras => electivas(otras)
  }

  def cursos(materias: List[Materia]): List[Curso] = materias match {
    case Nil => Nil
    case materia::otras => append(materia.cursos, cursos(otras))
  }

  def inscriptos(cursos: List[Curso]): List[Alumno] = cursos match {
    case Nil => Nil
    case curso::otros => append(curso.inscriptos, inscriptos(otros))
  }

  def regulares(alumnos: List[Alumno]): List[Alumno] = alumnos match {
    case Nil => Nil
    case alumno::otros if alumno.regular => alumno::regulares(otros)
    case _::otros => regulares(otros)
  }

  def legajos(alumnos: List[Alumno]): List[String] = alumnos match {
    case Nil => Nil
    case alumno::otros => alumno.legajo::legajos(otros)
  }

  private def append[T](list: List[T], other: List[T]): List[T] = list match {
    case Nil => other
    case head::tail => head::append(tail, other)
  }
  
}
