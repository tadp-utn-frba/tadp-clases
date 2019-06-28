package conversions

import conversions.Conversions.Implicits._
import conversions.Conversions.{FacebookUser, TwitterUser}
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._

class ConvertionsTest extends WordSpec with Matchers {

  "Convertions" should {

    "convert users" in {
      var twUser = TwitterUser(1, "Pepe")
      var fbUser = FacebookUser(twUser.id.toString, twUser.name)

      fbUser shouldEqual FacebookUser("1", "Pepe")
    }

    "convert users implicitly" in {
      var twUser = TwitterUser(1, "Pepe")
      var fbUser: FacebookUser = twUser

      fbUser shouldEqual FacebookUser("1", "Pepe")
    }

    "scala lo usa para las duraciones" in {
      val duration: Duration = (2, SECONDS)
      duration shouldEqual FiniteDuration(2, SECONDS)
    }

  }

}
