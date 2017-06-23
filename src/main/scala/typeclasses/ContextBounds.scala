package typeclasses

object ContextBounds {

  trait Saludador[T] {
    def saluda(t: T): String
  }

  def saludo[A: Saludador](a: A) = implicitly[Saludador[A]].saluda(a)

}