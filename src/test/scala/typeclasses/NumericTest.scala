package typeclasses

import org.scalatest.{Matchers, WordSpec}

class NumericTest extends WordSpec with Matchers {

  "Numeric" should {

    "puedo sumar" in {
      val x: Int = 1
      val y: Int = 2

      List(x, y).sum shouldEqual 3
    }

    "usuario puede ser numeric" in {
      case class User(id: Long)

      implicit object NumUser extends Numeric[User] {
        override def plus(x: User, y: User): User = User(x.id + y.id)

        override def minus(x: User, y: User): User = ???

        override def times(x: User, y: User): User = ???

        override def negate(x: User): User = ???

        override def fromInt(x: Int): User = User(x)

        override def toInt(x: User): Int = ???

        override def toLong(x: User): Long = ???

        override def toFloat(x: User): Float = ???

        override def toDouble(x: User): Double = ???

        override def compare(x: User, y: User): Int = ???
      }

      List(User(1), User(2)).sum shouldEqual User(3)
    }

    "power operator" in {
      implicit class NumericPower[A: Numeric](num: A) {

        import Numeric.Implicits.infixNumericOps

        def **[B](exp: B)(implicit n: Numeric[B]): Double = {
          math.pow(num.toDouble, exp.toDouble)
        }
      }

      val dos: Int = 2
      val tres: Double = 3
      (dos ** tres) shouldEqual 8

      case class User(id: Long)

      implicit object NumUser extends Numeric[User] {
        override def plus(x: User, y: User): User = ???

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

      val x: Int = 2
      val y: User = User(3)
      val res = x ** y

      res shouldEqual 8
    }

  }
}
