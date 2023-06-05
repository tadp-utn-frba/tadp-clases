package usercode.b

import stdlib.b.tad.{add, append, head, isEmpty, tail}
import usercode.b.tad.{Alumno, Curso, Materia}

package object tad {
  def electivas(materias: List[Materia]): List[Materia] =
    if (isEmpty(materias)) List.empty
    else if (head(materias).electiva) add(electivas(tail(materias)), head(materias))
    else electivas(tail(materias))

  def cursos(materias: List[Materia]): List[Curso] =
    if (isEmpty(materias)) List.empty
    else append(head(materias).cursos, cursos(tail(materias)))

  def inscriptos(cursos: List[Curso]): List[Alumno] =
    if (isEmpty(cursos)) List.empty
    else append(head(cursos).inscriptos, inscriptos(tail(cursos)))

  def regulares(alumnos: List[Alumno]): List[Alumno] =
    if (isEmpty(alumnos)) List.empty
    else if (head(alumnos).regular) add(regulares(tail(alumnos)), head(alumnos))
    else regulares(tail(alumnos))

  def legajos(alumnos: List[Alumno]): List[String] =
    if (isEmpty(alumnos)) List.empty
    else add(legajos(tail(alumnos)), head(alumnos).legajo)
}
