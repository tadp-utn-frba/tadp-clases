package tadp

import org.scalatest.{Matchers, WordSpec}
import tadp.clase.Convertions.Implicits._
import tadp.clase.Convertions.{UnTipoDeUser, UsuarioFramework}


class ConvertionsTest extends WordSpec with Matchers {

  "Convertions" should {
    "convert users" in {
      var miUsuario = new UnTipoDeUser(1, "Pepe")
      var usuarioFramework: UsuarioFramework =
        miUsuario

      usuarioFramework shouldEqual UsuarioFramework("1", "Pepe")
    }

    "todo a string" in {
      def mensaje(text: String): String = s"$text!"

      var miUsuario = new UnTipoDeUser(1, "Pepe")

      mensaje(miUsuario) shouldEqual "UnTipoDeUser(1,Pepe)!"
    }

    "importantear" in {
      "Hola".toLowerCase shouldEqual "hola" //???
      "Hola".importante.toLowerCase() shouldEqual "hola!"
      "Hola".importante2.toLowerCase() shouldEqual "hola!"
    }
  }

}
