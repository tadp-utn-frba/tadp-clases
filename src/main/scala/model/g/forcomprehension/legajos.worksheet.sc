import model.e.objetos.*

for {
  materia <- todasLasMaterias if materia.electiva
  curso <- materia.cursos
  alumno <- curso.inscriptos if alumno.regular
} yield alumno.legajo

// Se reescribe a:
// todasLasMaterias
//   .filter(materia => materia.electiva)
//   .flatMap(materia => materia.cursos
//       .flatMap(curso => curso.inscriptos
//           .filter(alumno => alumno.regular)
//           .map(alumno => alumno.legajo)
//       )
//   )
