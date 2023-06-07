package model.abcd

import model.abcd.funcional.{Materia, Curso, Alumno, Docente}

package object funcional {
  val todasLasMaterias: List[Materia] = List(
    Materia(nombre = "Algoritmos y Estructuras de Datos", electiva = false, cursos = List(
      Curso(codigo = 1041, titular = Docente(apellido = "Samuel", email = null), inscriptos = List(
        Alumno(apellido = "Molina", legajo = "234567", regular = false),
        Alumno(apellido = "Romero", legajo = "345678", regular = true),
        Alumno(apellido = "Otamendi", legajo = "456789", regular = true),
        Alumno(apellido = "Tagliafico", legajo = "123456", regular = false),
      )),
      Curso(codigo = 1042, titular = null, inscriptos = List(
        Alumno(apellido = "Martinez", legajo = "123456", regular = true),
        Alumno(apellido = "Armani", legajo = "789012", regular = false),
      )),
    )),
    Materia(nombre = "Paradigmas de Programación", electiva = false, cursos = List(
      Curso(codigo = 2004, titular = Docente(apellido = "Aimar", email = "paimar@frba.utn.edu.ar"), inscriptos = List(
        Alumno(apellido = "De Paul", legajo = "234567", regular = true),
        Alumno(apellido = "Fernandez", legajo = "345678", regular = false),
        Alumno(apellido = "Mac Allister", legajo = "456789", regular = false),
        Alumno(apellido = "Paredes", legajo = "123456", regular = true),
      )),
    )),
    Materia(nombre = "Técnicas Avanzadas de Programación", electiva = true, cursos = List(
      Curso(codigo = 3001, titular = Docente(apellido = "Scaloni", email = "lscaloni@frba.utn.edu.ar"), inscriptos = List(
        Alumno(apellido = "Messi", legajo = "234567", regular = true),
        Alumno(apellido = "Di Maria", legajo = "123456", regular = true),
        Alumno(apellido = "Alvarez", legajo = "345678", regular = false),
        Alumno(apellido = "Martinez", legajo = "456789", regular = true),
      )),
    ))
  )
}
