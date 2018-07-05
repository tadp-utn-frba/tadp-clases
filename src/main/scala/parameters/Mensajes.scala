package parameters

trait Formateador {
  def formatear(mensaje: String): String
}

object Formateador {
  implicit val importante = new Importante
}

object ImplicitsImport {
  // implicit val importante = new Cruz
}

class Importante extends Formateador {
  def formatear(mensaje: String) =
    s"$mensaje!"
}

class Cruz extends Formateador {
  override def formatear(mensaje: String): String =
    s"XXX${mensaje}XXX"
}

class Padre {
  //  implicit val cruz = new Cruz
}

class Mensajes extends Padre {
  def nuevoMensaje(mensaje: String)(implicit formateador: Formateador) =
    formateador.formatear(mensaje)

  def lowerMensaje(mensaje: String)(implicit formateador: Formateador): String = {
    nuevoMensaje(mensaje.toLowerCase())
  }
}