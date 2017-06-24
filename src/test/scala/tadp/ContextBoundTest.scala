package tadp

import org.scalatest.{Matchers, WordSpec}
import tadp.clase.{ContextBound, Importanteable}

class ContextBoundTest extends WordSpec with Matchers {

  "ContextBound" should {
    "usa el import" in {
      implicit val importantableString =
        new Importanteable[String]()

      ContextBound.necesitoImportante("hola") shouldEqual "ok"
    }

  }

}
