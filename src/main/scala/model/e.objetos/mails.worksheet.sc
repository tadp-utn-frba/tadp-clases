import model.e.objetos.*

def mailAlCurso(curso: Curso): Unit = {
  val talVezUnDocente = curso.titular
  if (talVezUnDocente != null) {
    val talVezUnMail = talVezUnDocente.email
    if (talVezUnMail != null) {
      val talVezUnResultado = enviarEmailA(talVezUnMail)
      if (talVezUnResultado != null) {
        println(s"Se envió a $talVezUnResultado")
        return
      }
    }
  }
  println(s"No se pudo enviar el mail a ${curso.codigo}")
}

todasLasMaterias.flatMap(_.cursos).foreach(mailAlCurso)
