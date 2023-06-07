import model.f.monadas.*

def mailAlCurso(curso: Curso): Unit = {
  val mensaje = curso.titular
    .flatMap(docente => docente.email)
    .flatMap(enviarEmailA)
    .fold(s"No se pudo enviar el mail a ${curso.codigo}")(
      resultado => s"Se envió a $resultado"
    )

  println(mensaje)
}

todasLasMaterias.flatMap(_.cursos).foreach(mailAlCurso)
