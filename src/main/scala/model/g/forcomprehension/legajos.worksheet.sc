import model.e.objetos.*

for {
  materia <- todasLasMaterias if materia.electiva
  curso <- materia.cursos
  alumno <- curso.inscriptos if alumno.regular
} yield alumno.legajo
