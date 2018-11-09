package typeclasses

import org.scalatest.{Matchers, WordSpec}

class ContextBoundTest extends WordSpec with Matchers {

  trait Combinador[A] {
    def combinar(a1: A, a2: A): A
  }

  implicit object CombinadorInt extends Combinador[Int] {
    override def combinar(i1: Int, i2: Int): Int =
      i1 + i2
  }

  "ContextBound" should {

    "usando implicits" in {
      def calcular[A](a1: A, a2: A)(implicit c: Combinador[A]) =
        c.combinar(a1, a2)

      calcular(1, 2) shouldEqual 3
    }














    "usando context bound" in {
      def calcular[A: Combinador](a1: A, a2: A): A =
        implicitly[Combinador[A]].combinar(a1, a2)

      calcular(1, 2) shouldEqual 3
    }

  }

}
