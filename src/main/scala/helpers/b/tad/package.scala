package helpers.b

import listas.b.tad.{head, isEmpty, tail}
import model.abcd.funcional.{Materia, Curso, Alumno}

package object tad {
  def electivas(materias: List[Materia]): List[Materia] =
    if (isEmpty(materias)) List.empty
    else if (head(materias).electiva) head(materias)::electivas(tail(materias))
    else electivas(tail(materias))

  def cursos(materias: List[Materia]): List[Curso] =
    if (isEmpty(materias)) List.empty
    else append(head(materias).cursos, cursos(tail(materias)))

  def inscriptos(cursos: List[Curso]): List[Alumno] =
    if (isEmpty(cursos)) List.empty
    else append(head(cursos).inscriptos, inscriptos(tail(cursos)))

  def regulares(alumnos: List[Alumno]): List[Alumno] =
    if (isEmpty(alumnos)) List.empty
    else if (head(alumnos).regular) head(alumnos)::regulares(tail(alumnos))
    else regulares(tail(alumnos))

  def legajos(alumnos: List[Alumno]): List[String] =
    if (isEmpty(alumnos)) List.empty
    else head(alumnos).legajo::legajos(tail(alumnos))

  private def append[T](list: List[T], other: List[T]): List[T] =
    if (isEmpty(list)) other
    else head(list)::append(tail(list), other)
}
