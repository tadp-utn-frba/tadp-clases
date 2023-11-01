import model.f.monadas._

import scala.util.Try

def anotar(curso: Curso): Curso =
  try {
    curso.inscribir(unAlumno)
  } catch {
    case _: NoAutorizadoException =>
      jefeDeCatedra.autorizar(unAlumno, curso)
      curso.inscribir(unAlumno)
  }

todasLasMaterias.flatMap(_.cursos).map(anotar).foreach(println)

