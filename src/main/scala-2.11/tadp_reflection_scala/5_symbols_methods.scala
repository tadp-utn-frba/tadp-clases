package tadp_reflection_scala.symbols

class D[T] {
  def m[U>:T](x: T)(y: U): Int = ???
}