package model.f

package object monadas {
  val jefeDeCatedra: Docente = Docente(apellido = "Scaloni", email = Some("lscaloni@frba.utn.edu.ar"))

  val todasLasMaterias: List[Materia] = List(
    Materia(nombre = "Algoritmos y Estructuras de Datos", electiva = false, cursos = List(
      Curso(codigo = 1041, titular = None, inscriptos = List(
        Alumno(apellido = "Martinez", legajo = "123456", regular = true),
        Alumno(apellido = "Rulli", legajo = "789012", regular = false),
      )),
      Curso(codigo = 1042, titular = Some(Docente(apellido = "Samuel", email = Some("wsamuel@frba.utn.edu.ar"))), inscriptos = List(
        Alumno(apellido = "Molina", legajo = "234567", regular = false),
        Alumno(apellido = "Romero", legajo = "345678", regular = true),
        Alumno(apellido = "Otamendi", legajo = "456789", regular = true),
        Alumno(apellido = "Tagliafico", legajo = "123456", regular = false),
      )),
    )),
    Materia(nombre = "Paradigmas de Programación", electiva = false, cursos = List(
      Curso(codigo = 2004, titular = Some(Docente(apellido = "Aimar", email = None)), inscriptos = List(
        Alumno(apellido = "De Paul", legajo = "234567", regular = true),
        Alumno(apellido = "Fernandez", legajo = "345678", regular = false),
        Alumno(apellido = "Mac Allister", legajo = "456789", regular = false),
        Alumno(apellido = "Paredes", legajo = "123456", regular = true),
      )),
    )),
    Materia(nombre = "Técnicas Avanzadas de Programación", electiva = true, cursos = List(
      Curso(codigo = 3001, titular = Some(jefeDeCatedra), inscriptos = List(
        Alumno(apellido = "Messi", legajo = "234567", regular = true),
        Alumno(apellido = "Di Maria", legajo = "123456", regular = true),
        Alumno(apellido = "Alvarez", legajo = "345678", regular = false),
        Alumno(apellido = "Martinez", legajo = "456789", regular = true),
      )),
    ))
  )

  val unAlumno: Alumno = Alumno(apellido = "Armani", legajo = "123456", regular = true)

  def enviarEmailA(mail: String): Option[String] = Option.when(mail.contains("@"))(mail)
}
