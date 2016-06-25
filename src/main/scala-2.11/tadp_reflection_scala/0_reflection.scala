package tadp_reflection_scala

object Reflection0 {
  trait T { def f: Int }
  class C extends T {
    def m(x: Int) = x
    var f = 5
  }
}