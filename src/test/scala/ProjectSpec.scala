import org.scalatest.matchers.should.Matchers._
import org.scalatest.freespec.AnyFreeSpec

class ProjectSpec extends AnyFreeSpec {
  val laVerdad = true

  "Este proyecto" - {

    "cuando está correctamente configurado" - {
      "debería resolver las dependencias y pasar este test" in {
        laVerdad shouldBe true
      }
    }
  }

}
