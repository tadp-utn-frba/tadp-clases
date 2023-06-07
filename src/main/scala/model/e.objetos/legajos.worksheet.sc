import model.e.objetos.*

todasLasMaterias
  .filter(materia => materia.electiva)
  .flatMap(materia => materia.cursos)
  .flatMap(curso => curso.inscriptos)
  .filter(alumno => alumno.regular)
  .map(alumno => alumno.legajo)

