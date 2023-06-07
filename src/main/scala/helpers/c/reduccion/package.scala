package helpers.c

import listas.c.reduccion.foldLeft
import model.abcd.funcional.{Materia, Curso, Alumno}

package object reduccion {
  def electivas: List[Materia] => List[Materia] =
    foldLeft(List.empty[Materia])(
      (lista, materia) => if (materia.electiva) materia::lista else lista
    )

  def cursos: List[Materia] => List[Curso] =
    foldLeft(List.empty[Curso])(
      (lista, materia) => append(lista, materia.cursos)
    )

  def inscriptos: List[Curso] => List[Alumno] =
    foldLeft(List.empty[Alumno])(
      (lista, curso) => append(lista, curso.inscriptos)
    )

  def regulares: List[Alumno] => List[Alumno] =
    foldLeft(List.empty[Alumno])(
      (lista, alumno) => if (alumno.regular) alumno::lista else lista
    )

  def legajos: List[Alumno] => List[String] =
    foldLeft(List.empty[String])(
      (lista, alumno) => alumno.legajo::lista
    )

  private def append[T](izq: List[T], der: List[T]): List[T] =
    foldLeft[List[T], T](der)((lista, elemento) => elemento::lista)(izq)
}
