package tadp_reflection_scala
import scala.reflect.runtime.universe._

object reflection_1 {
  // Igualdad
  
  
  // true ~> A es el mismo tipo que A
  typeOf[Example.A] =:= typeOf[Example.A]         //> res0: Boolean = true
  // false ~> A no es el mismo tipo que B
  typeOf[Example.A] =:= typeOf[Example.B]         //> res1: Boolean = false
  // false ~> == no chequea alias
  typeOf[Example.A] == typeOf[Example.A2]         //> res2: Boolean = false
  // true ~> =:= se da cuenta que son el mismo tipo
  typeOf[Example.A] =:= typeOf[Example.A2]        //> res3: Boolean = true

  // Subtipado
  
  // true ~> A es subtipo de A
  typeOf[Example.A] <:< typeOf[Example.A]         //> res4: Boolean = true
  // true ~> B es subtipo de A
  typeOf[Example.B] <:< typeOf[Example.A]         //> res5: Boolean = true
  // false ~> A no es subtipo de B
  typeOf[Example.A] <:< typeOf[Example.B]         //> res6: Boolean = false
  // false ~> Int no es realmente subtipo de Long
  typeOf[Int] <:< typeOf[Long]                    //> res7: Boolean = false
  // true ~> pero Sí hace algo parecido
  typeOf[Int] weak_<:< typeOf[Long]               //> res8: Boolean = true
  // true
  weakTypeOf[List[Example.B]] <:< weakTypeOf[List[Example.A]]
                                                  //> res9: Boolean = true
  // false
  weakTypeOf[List[Example.A]] <:< weakTypeOf[List[Example.B]]
                                                  //> res10: Boolean = false

  // Declaraciones
  // returns SynchronizedOps(constructor A, method m, value f, value f)
  typeOf[Example.A].declarations                  //> res11: reflect.runtime.universe.MemberScope = SynchronizedOps(constructor A
                                                  //| , method m, value f, value f)
  // returns false
  typeOf[Example.A].takesTypeArgs                 //> res12: Boolean = false
  // returns List()
  typeOf[Example.A].typeParams                    //> res13: List[reflect.runtime.universe.Symbol] = List()

}