package conversions

import java.util.concurrent.TimeUnit

import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._
import scala.language.{implicitConversions, postfixOps}

class ExtensionsTest extends WordSpec with Matchers {

  "Extensions" should {

    def hashToUrl(hash: String): String =
      "https://github.com/tadp-utn-frba/tadp-utn-frba.github.io/pull/" + hash.stripPrefix("#")

    "puedo convertir un hash de pull request a una URL" in {
      hashToUrl("#34") shouldEqual
        "https://github.com/tadp-utn-frba/tadp-utn-frba.github.io/pull/34"
    }

    // Quisiera agregarle ese método a todos los strings:
    // "#34".hashToUrl

    class PullRequestHash(s: String) {
      def pullRequestUrl: String = hashToUrl(s)
    }

    "puedo wrappear un string con hash" in {
      new PullRequestHash("#34").pullRequestUrl shouldEqual
        "https://github.com/tadp-utn-frba/tadp-utn-frba.github.io/pull/34"
    }

    "puedo convertir un strings a un PullRequestHash" in {
      implicit def convertirAConHash(s: String): PullRequestHash =
        new PullRequestHash(s)

      "#34".pullRequestUrl shouldEqual
        "https://github.com/tadp-utn-frba/tadp-utn-frba.github.io/pull/34"
    }

    "puedo agregar hashToUrl a los strings" in {
      import Extensions._

      "#34".pullRequestUrl shouldEqual
        "https://github.com/tadp-utn-frba/tadp-utn-frba.github.io/pull/34"
    }

    "duration" in {
      val manual = new FiniteDuration(2, TimeUnit.SECONDS)
      val infixExtension = 2 seconds

      manual shouldEqual infixExtension
    }
  }

}
