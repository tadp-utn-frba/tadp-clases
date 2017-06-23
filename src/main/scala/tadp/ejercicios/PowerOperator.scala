package tadp.ejercicios

class PowerOperator {
  implicit class NumericPower[A: Numeric](num: A) {
    import Numeric.Implicits.infixNumericOps

    def **[B: Numeric, A: Numeric](exp: B): Double = {
      math.pow(num.toDouble, exp.toDouble)
    }
//    def **[B: Numeric](exp: B): Double = {
//      math.pow(num.toDouble, exp.toDouble)
//    }
  }
}
