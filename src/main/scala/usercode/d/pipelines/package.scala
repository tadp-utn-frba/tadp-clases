package usercode.d

import stdlib.d.pipelines.{filter, flatMap, map}
import usercode.b.tad.{Alumno, Curso, Materia}

package object pipelines {
  def electivas: List[Materia] => List[Materia] =
    filter(materia => materia.electiva)

  def cursos: List[Materia] => List[Curso] =
    flatMap(materia => materia.cursos)

  def inscriptos: List[Curso] => List[Alumno] =
    flatMap(curso => curso.inscriptos)

  def regulares: List[Alumno] => List[Alumno] =
    filter(alumno => alumno.regular)

  def legajos: List[Alumno] => List[String] =
    map(alumno => alumno.legajo)
}
