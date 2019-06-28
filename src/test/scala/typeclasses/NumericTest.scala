package typeclasses

import org.scalatest.{Matchers, WordSpec}

class NumericTest extends WordSpec with Matchers {

  "Numeric" should {

    "puedo sumar" in {
      List(1, 2).sum shouldEqual 3
      List(1.5, 2.6).sum shouldEqual 4.1
    }

    "usuario puede ser numeric" in {
      implicit object NumericString extends Numeric[String] {
        override def plus(x: String, y: String): String = x + y
        override def zero: String = ""

        // hack, nunca existió...
        override def fromInt(x: Int): String = ???
        override def minus(x: String, y: String): String = ???
        override def times(x: String, y: String): String = ???
        override def negate(x: String): String = ???
        override def toInt(x: String): Int = ???
        override def toLong(x: String): Long = ???
        override def toFloat(x: String): Float = ???
        override def toDouble(x: String): Double = ???
        override def compare(x: String, y: String): Int = ???
      }

      List("1", "2").sum shouldEqual "12"
    }

    "power operator" in {
      val dos: Int = 2
      val tres: Double = 3

      math.pow(dos, tres) shouldEqual 8
      
      //      (dos ** tres) shouldEqual 8


      //      implicit class NumericPower[A: Numeric](num: A) {
      //
      //        import Numeric.Implicits.infixNumericOps
      //
      //        def **[B](exp: B)(implicit n: Numeric[B]): Double = {
      //          math.pow(num.toDouble, exp.toDouble)
      //        }
      //      }
      //
      //      val dos: Int = 2
      //      val tres: Double = 3
      //      (dos ** tres) shouldEqual 8
      //
      //      case class User(id: Long)
      //
      //      implicit object NumUser extends Numeric[User] {
      //        override def plus(x: User, y: User): User = ???
      //
      //        override def minus(x: User, y: User): User = ???
      //
      //        override def times(x: User, y: User): User = ???
      //
      //        override def negate(x: User): User = ???
      //
      //        override def fromInt(x: Int): User = ???
      //
      //        override def toInt(x: User): Int = ???
      //
      //        override def toLong(x: User): Long = ???
      //
      //        override def toFloat(x: User): Float = ???
      //
      //        override def toDouble(x: User): Double = x.id.toDouble
      //
      //        override def compare(x: User, y: User): Int = ???
      //      }
      //
      //      val x: Int = 2
      //      val y: User = User(3)
      //      val res = x ** y
      //
      //      res shouldEqual 8
    }

  }
}
