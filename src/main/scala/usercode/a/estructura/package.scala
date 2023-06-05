package usercode.a

import stdlib.a.{MyNil, MyNode}
import stdlib.a.estructura.MyList
import usercode.a.estructura.{Alumno, Curso, Materia}

package object estructura {
  def electivas(materias: MyList[Materia]): MyList[Materia] = materias match {
    case MyNil() => MyNil()
    case MyNode(materia, otrasMaterias) if materia.electiva => MyNode(materia, electivas(otrasMaterias))
    case MyNode(_, otrasMaterias) => electivas(otrasMaterias)
  }

  def cursos(materias: MyList[Materia]): MyList[Curso] = materias match {
    case MyNil() => MyNil()
    case MyNode(materia, materiasRestantes) => append(materia.cursos, cursos(materiasRestantes))
  }

  def inscriptos(cursos: MyList[Curso]): MyList[Alumno] = cursos match {
    case MyNil() => MyNil()
    case MyNode(curso, cursosRestantes) => append(curso.inscriptos, inscriptos(cursosRestantes))
  }

  def regulares(alumnos: MyList[Alumno]): MyList[Alumno] = alumnos match {
    case MyNil() => MyNil()
    case MyNode(alumno, alumnosRestantes) if alumno.regular => MyNode(alumno, regulares(alumnosRestantes))
    case MyNode(_, alumnosRestantes) => regulares(alumnosRestantes)
  }

  def legajos(alumnos: MyList[Alumno]): MyList[String] = alumnos match {
    case MyNil() => MyNil()
    case MyNode(alumno, alumnosRestantes) => MyNode(alumno.legajo, legajos(alumnosRestantes))
  }

  private def append[T](list: MyList[T], other: MyList[T]): MyList[T] = list match {
    case MyNil() => MyNil()
    case MyNode(x, xs) => MyNode(x, append(xs, other))
  }
}
