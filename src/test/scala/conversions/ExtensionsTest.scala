package conversions

import org.scalatest.{Matchers, WordSpec}

class ExtensionsTest extends WordSpec with Matchers {

  import Extensions._

  "Extensions" should {
    "importantear" in {
      "Hola".toLowerCase shouldEqual "hola" //???
      "Hola".importante.toLowerCase() shouldEqual "hola!"
      "Hola".importante2.toLowerCase() shouldEqual "hola!"
    }
  }

}
