package tadp.ejercicios

class PowerOperator {

  implicit class NumericPower[A: Numeric](num: A) {
    import Numeric.Implicits.infixNumericOps

    def **[B](exp: B)(implicit n: Numeric[B]): Double = {
      math.pow(num.toDouble, exp.toDouble)
    }
  }

  //  implicit class Power[A: Numeric](a: A) {
  //    import Numeric.Implicits.infixNumericOps
  //
  //    def **[B: Numeric](b: B) = {
  //      a + b
  ////      val aDouble = implicitly[Numeric[A]].toDouble(a)
  ////      math.pow(a.toDouble(), b.toDouble())
  //    }
  //  }

  val d: Int = 7
  val g: Double = 8
  d ** g


}
