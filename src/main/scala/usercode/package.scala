import stdlib.a.estructura.{MyList, createList}
import usercode.a.estructura.{Alumno, Curso, Docente, Materia}

package object usercode {
  val todasLasMaterias: MyList[Materia] = createList(
    Materia(nombre = "Algoritmos y Estructuras de Datos", electiva = false, cursos = createList(
      Curso(codigo = 1041, titular = null, inscriptos = createList(
        Alumno(apellido = "Martinez", legajo = "123456", regular = true),
      )),
      Curso(codigo = 1042, titular = Docente(apellido = "Samuel", email = null), inscriptos = createList(
        Alumno(apellido = "Molina", legajo = "234567", regular = false),
        Alumno(apellido = "Romero", legajo = "345678", regular = true),
        Alumno(apellido = "Otamendi", legajo = "456789", regular = true),
        Alumno(apellido = "Tagliafico", legajo = "123456", regular = false),
      )),
    )),
    Materia(nombre = "Paradigmas de Programación", electiva = false, cursos = createList(
      Curso(codigo = 2004, titular = Docente(apellido = "Aimar", email = "paimar@frba.utn.edu.ar"), inscriptos = createList(
        Alumno(apellido = "De Paul", legajo = "234567", regular = true),
        Alumno(apellido = "Fernandez", legajo = "345678", regular = false),
        Alumno(apellido = "Mac Allister", legajo = "456789", regular = false),
        Alumno(apellido = "Paredes", legajo = "123456", regular = true),
      )),
    )),
    Materia(nombre = "Técnicas Avanzadas de Programación", electiva = true, cursos = createList(
      Curso(codigo = 3001, titular = Docente(apellido = "Scaloni", email = "lscaloni@frba.utn.edu.ar"), inscriptos = createList(
        Alumno(apellido = "Messi", legajo = "234567", regular = true),
        Alumno(apellido = "Di Maria", legajo = "123456", regular = true),
        Alumno(apellido = "Alvarez", legajo = "345678", regular = false),
        Alumno(apellido = "Martinez", legajo = "456789", regular = true),
      )),
    )),
  )
}
