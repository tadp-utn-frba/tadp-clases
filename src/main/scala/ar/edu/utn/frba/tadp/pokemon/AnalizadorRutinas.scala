package ar.edu.utn.frba.tadp.pokemon

object AnalizadorRutinas {
  def hacerRutina(rutina: Rutina)(estado: Estado): Estado =
    rutina.actividades.foldRight(estado)((actividad, estado) => estado.realizarActividad(actividad))

  def mejorRutinaSegun[O : Ordering](condicion: Estado => O)(rutinas: List[Rutina])(estado: Estado): Option[Rutina] =
    rutinas.maxByOption(rutina => condicion(hacerRutina(rutina)(estado)))
}

case class Rutina(nombre: String, actividades: List[Actividad])