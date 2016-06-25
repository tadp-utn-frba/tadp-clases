import scala.reflect.runtime.universe._

class A {
  def m = 5
  val f = 3
}

class B extends A
type A2 = A
