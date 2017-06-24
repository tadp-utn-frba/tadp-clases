package tadp

import org.scalatest.{Matchers, WordSpec}
import tadp.Power.FromDouble

object Power {

  trait FromDouble[A] {
    def fromDouble(x: Double): A
  }

  implicit class NumericPower[A:Numeric](val x: A) {
    import Numeric.Implicits.infixNumericOps

    def **[B,C](y: B)(implicit n2: Numeric[B], nc: FromDouble[C]): C = {
      nc.fromDouble(math.pow(x.toDouble(), n2.toDouble(y)))
    }
  }

}

class NumericTest extends WordSpec with Matchers {

  "Numeric" should {

    "puedo sumar" in {
      val x: Int = 1
      val y: Int = 2

      List(x, y).sum
    }


    "power" in {
      case class User(id: Long)
      implicit object NumUser extends Numeric[User] {
        override def plus(x: User, y: User): User = User(x.id + y.id)

        override def minus(x: User, y: User): User = ???

        override def times(x: User, y: User): User = ???

        override def negate(x: User): User = ???

        override def fromInt(x: Int): User = ???

        override def toInt(x: User): Int = ???

        override def toLong(x: User): Long = ???

        override def toFloat(x: User): Float = ???

        override def toDouble(x: User): Double = x.id.toDouble

        override def compare(x: User, y: User): Int = ???
      }

//      implicit object DoubleToUser extends FromDouble[User] {
//        override def fromDouble(x: Double): User =
//          User(x.toLong)
//      }

      implicit object DoubleToInt extends FromDouble[Int] {
        override def fromDouble(x: Double): Int =
          x.toInt
      }

      import Power._
      val x: Int = 2
      val y: User = User(3)
      val res: Int = x ** y

      res shouldEqual 8
    }

  }

}
