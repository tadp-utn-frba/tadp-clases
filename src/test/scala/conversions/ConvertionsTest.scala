package conversions

import conversions.Conversions.Implicits._
import conversions.Conversions.{FacebookUser, TwitterUser}
import org.scalatest.{Matchers, WordSpec}

class ConvertionsTest extends WordSpec with Matchers {

  "Convertions" should {
    "convert users" in {
      var twUser = new TwitterUser(1, "Pepe")
      var fbUser: FacebookUser = twUser

      fbUser shouldEqual FacebookUser("1", "Pepe")
    }
  }

}
