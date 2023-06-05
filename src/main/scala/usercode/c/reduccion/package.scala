package usercode.c

import scala.annotation.tailrec
import stdlib.b.tad.{add, append}
import stdlib.c.reduccion.{foldLeft, foldRight}
import usercode.b.tad.{Alumno, Curso, Materia}

package object reduccion {
  def electivas: List[Materia] => List[Materia] =
    foldLeft(List.empty[Materia])((acum, materia) => if (materia.electiva) add(acum, materia) else acum)

  def cursos: List[Materia] => List[Curso] =
    foldRight(List.empty[Curso])((materia, acum) => append(acum, materia.cursos))

  def inscriptos: List[Curso] => List[Alumno] =
    foldLeft(List.empty[Alumno])((acum, curso) => append(acum, curso.inscriptos))

  def regulares: List[Alumno] => List[Alumno] =
    foldRight(List.empty[Alumno])((alumno, acum) => if (alumno.regular) add(acum, alumno) else acum)

  def legajos: List[Alumno] => List[String] =
    foldLeft(List.empty[String])((acum, alumno) => add(acum, alumno.legajo))
}
