package tadp_reflection_scala

object Example {
  class A {
    def m = 5
    val f = 3
  }

  class B extends A
  type A2 = A
}

