package tadp_reflection_scala
import scala.reflect.runtime.universe._

object reflection_1 {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(169); val res$0 = 

  // Igualdad
  typeOf[A] =:= typeOf[A];System.out.println("""res0: <error> = """ + $show(res$0));$skip(66); val res$1 =  // true ~> A es el mismo tipo que A
  typeOf[A] =:= typeOf[B];System.out.println("""res1: <error> = """ + $show(res$1));$skip(58); val res$2 =  // false ~> A no es el mismo tipo que B
  typeOf[A] == typeOf[A2];System.out.println("""res2: <error> = """ + $show(res$2));$skip(77); val res$3 =  // false ~> == no chequea alias
  typeOf[A] =:= typeOf[A2];System.out.println("""res3: <error> = """ + $show(res$3));$skip(71); val res$4 =  // true ~> =:= se da cuenta que son el mismo tipo

  // Subtipado
  typeOf[A] <:< typeOf[A];System.out.println("""res4: <error> = """ + $show(res$4));$skip(55); val res$5 =  // true ~> A es subtipo de A
  typeOf[B] <:< typeOf[A];System.out.println("""res5: <error> = """ + $show(res$5));$skip(59); val res$6 =  // true ~> B es subtipo de A
  typeOf[A] <:< typeOf[B];System.out.println("""res6: <error> = """ + $show(res$6));$skip(79); val res$7 =  // false ~> A no es subtipo de B
  typeOf[Int] <:< typeOf[Long];System.out.println("""res7: Boolean = """ + $show(res$7));$skip(74); val res$8 =  // false ~> Int no es realmente subtipo de Long
  typeOf[Int] weak_<:< typeOf[Long];System.out.println("""res8: Boolean = """ + $show(res$8));$skip(54); val res$9 =  // true ~> pero Sí hace algo parecido
  weakTypeOf[List[B]] <:< weakTypeOf[List[A]];System.out.println("""res9: <error> = """ + $show(res$9));$skip(55); val res$10 =  // true
  weakTypeOf[List[A]] <:< weakTypeOf[List[B]];System.out.println("""res10: <error> = """ + $show(res$10));$skip(115); val res$11 =  // false

  // Declaraciones
  typeOf[A].declarations;System.out.println("""res11: reflect.runtime.universe.MemberScope = """ + $show(res$11));$skip(43); val res$12 =  // returns SynchronizedOps(constructor A, method m, value f, value f)
  typeOf[A].takesTypeArgs;System.out.println("""res12: Boolean = """ + $show(res$12));$skip(41); val res$13 =  // returns false
  typeOf[A].typeParams // returns List();System.out.println("""res13: List[reflect.runtime.universe.Symbol] = """ + $show(res$13))}

}
