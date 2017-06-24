package tadp.clase

import java.util.concurrent.TimeUnit

object Convertions {

  trait User {
    val id: Long
    val name: String
  }

  case class UnTipoDeUser(val id: Long, val name: String) extends User

  case class UsuarioFramework(id: String, name: String)

  object Implicits {
    implicit def usuario2Framework(user: User): UsuarioFramework =
      UsuarioFramework(user.id.toString, user.name)

    implicit def todoToString(any: Any): String =
      any.toString

    class Importanteador(s: String) {
      def importante = s"$s!"
      def toLowerCase = "fuck!"
    }

    implicit def agregarImportanteAString(string: String): Importanteador =
      new Importanteador(string)

    implicit class StringImportante(val s: String) extends AnyVal {
      def importante2 = s"$s!"
      def esMail: Boolean = ???
    }

    import scala.concurrent.duration._
    new FiniteDuration(2, TimeUnit.SECONDS)
    val tiempo = 2 seconds
  }

}
