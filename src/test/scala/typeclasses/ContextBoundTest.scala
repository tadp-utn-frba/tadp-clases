package typeclasses

import org.scalatest.{Matchers, WordSpec}

class ContextBoundTest extends WordSpec with Matchers {

  trait Calculable[A] {
    def calcular(a1: A, a2: A): A
  }

  implicit object IntCalculable extends Calculable[Int] {
    override def calcular(a1: Int, a2: Int): Int = a1 + a2
  }

  "ContextBound" should {

    "usando implicits" in {
      def calcular[A](a1: A, a2: A)(implicit calculable: Calculable[A]) =
        calculable.calcular(a1, a2)

      calcular(1, 2) shouldEqual 3
    }

    "usando context bound" in {
      def calcular[A: Calculable](a1: A, a2: A) =
        implicitly[Calculable[A]].calcular(a1, a2)

      calcular(1, 2) shouldEqual 3
    }

  }

}
