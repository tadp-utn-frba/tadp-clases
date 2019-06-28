package typeclasses

import org.scalatest.{Matchers, WordSpec}

class ContextBoundTest extends WordSpec with Matchers {

  trait Monoid[A] {
    def empty: A
    def combine(a1: A, a2: A): A
  }

  implicit object MonoidSumaInt extends Monoid[Int] {
    override def empty: Int = 0
    override def combine(i1: Int, i2: Int): Int =
      i1 + i2
  }

  implicit object MonoidConcatStrings extends Monoid[String] {
    override def empty: String = ""
    override def combine(a1: String, a2: String): String = a1 + a2
  }

  "ContextBound" should {

    "usando implicits" in {
      def calcular[A](a1: A, a2: A)(implicit c: Monoid[A]) =
        c.combine(a1, a2)

      calcular(1, 2) shouldEqual 3
      calcular("1", "2") shouldEqual "12"
    }


    "usando context bound" in {
      def calcular[A: Monoid](a1: A, a2: A): A = {
        val monoid = implicitly[Monoid[A]]

        monoid.combine(a1, a2)
      }

      calcular(1, 2) shouldEqual 3
      calcular("1", "2") shouldEqual "12"
    }

  }

}
