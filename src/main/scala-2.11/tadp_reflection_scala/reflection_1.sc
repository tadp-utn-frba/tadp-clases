package tadp_reflection_scala
import scala.reflect.runtime.universe._

object reflection_1 {

  // Igualdad
  typeOf[A] =:= typeOf[A] // true ~> A es el mismo tipo que A
  typeOf[A] =:= typeOf[B] // false ~> A no es el mismo tipo que B
  typeOf[A] == typeOf[A2] // false ~> == no chequea alias
  typeOf[A] =:= typeOf[A2] // true ~> =:= se da cuenta que son el mismo tipo

  // Subtipado
  typeOf[A] <:< typeOf[A] // true ~> A es subtipo de A
  typeOf[B] <:< typeOf[A] // true ~> B es subtipo de A
  typeOf[A] <:< typeOf[B] // false ~> A no es subtipo de B
  typeOf[Int] <:< typeOf[Long] // false ~> Int no es realmente subtipo de Long
  typeOf[Int] weak_<:< typeOf[Long] // true ~> pero Sí hace algo parecido
  weakTypeOf[List[B]] <:< weakTypeOf[List[A]] // true
  weakTypeOf[List[A]] <:< weakTypeOf[List[B]] // false

  // Declaraciones
  typeOf[A].declarations // returns SynchronizedOps(constructor A, method m, value f, value f)
  typeOf[A].takesTypeArgs // returns false
  typeOf[A].typeParams // returns List()

}