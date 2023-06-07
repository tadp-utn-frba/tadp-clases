package helpers.d

import listas.d.ordensuperior.{filter, flatMap, map}
import model.abcd.funcional.{Materia, Curso, Alumno}

package object ordensuperior {
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
