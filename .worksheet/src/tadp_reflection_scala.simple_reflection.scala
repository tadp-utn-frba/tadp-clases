package tadp_reflection_scala

object simple_reflection {

  trait T { def f: Int }
  class C extends T {
    def m(x: Int) = x
    var f = 5
  };import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(161); val res$0 = 

  classOf[Any];System.out.println("""res0: Class[Any](classOf[scala.Any]) = """ + $show(res$0));$skip(26); 
  val classC = classOf[C];System.out.println("""classC  : Class[tadp_reflection_scala.simple_reflection.C] = """ + $show(classC ));$skip(16); 
  val c = new C;System.out.println("""c  : tadp_reflection_scala.simple_reflection.C = """ + $show(c ));$skip(42); val res$1 = 

  classC.getName;System.out.println("""res1: String = """ + $show(res$1));$skip(62); val res$2 =  // returns "C" : String
  classC.getSuperclass;System.out.println("""res2: Class[?0] = """ + $show(res$2));$skip(61); val res$3 =  // returns java.lang.Object : Class[_]
  classC.getInterfaces;System.out.println("""res3: Array[Class[_]] = """ + $show(res$3));$skip(33); val res$4 =  // returns Array(T) : Array[Class[_]]
  classC.isEnum;System.out.println("""res4: Boolean = """ + $show(res$4));$skip(138); val res$5 =  // returns false
  //classC.isAnnotationPresent(classOf[SomeAnnotation]) // returns false

  classC.getDeclaredFields;System.out.println("""res5: Array[java.lang.reflect.Field] = """ + $show(res$5));$skip(73); val res$6 =  // returns Array(C.f) : Array[Field]
  classC.getFields;System.out.println("""res6: Array[java.lang.reflect.Field] = """ + $show(res$6));$skip(44);  // returns Array() ~> fields públicos (con heredados)
  val fieldF = classC.getDeclaredField("f");System.out.println("""fieldF  : java.lang.reflect.Field = """ + $show(fieldF ));$skip(43); val res$7 = 
  fieldF.getType;System.out.println("""res7: Class[?0] = """ + $show(res$7));$skip(130); val res$8 =  // returns int : Class[_]
  fieldF.getAnnotations;System.out.println("""res8: Array[java.lang.annotation.Annotation] = """ + $show(res$8));$skip(29);  // returns Array() : Array[Annotation]	fieldF.get(c) // Excepción! El campo es privado => no es accesible
  fieldF.setAccessible(true);$skip(29); val res$9 = 
  fieldF.get(c);System.out.println("""res9: Object = """ + $show(res$9));$skip(19);  // returns 5
  fieldF.set(c, 8);$skip(29); val res$10 = 
  fieldF.get(c);System.out.println("""res10: Object = """ + $show(res$10));$skip(93); val res$11 =  // returns 8

  classC.getDeclaredMethods;System.out.println("""res11: Array[java.lang.reflect.Method] = """ + $show(res$11));$skip(52);  // returns Array(C.f_$eq(int), C.f(), C.m(int)) : Array[Method]
  val methodM = classC.getMethod("m", classOf[Int]);System.out.println("""methodM  : java.lang.reflect.Method = """ + $show(methodM ));$skip(42); val res$12 = 
  methodM.getName;System.out.println("""res12: String = """ + $show(res$12));$skip(70); val res$13 =  // returns "m" : String
  methodM.getParameters;System.out.println("""res13: Array[java.lang.reflect.Parameter] = """ + $show(res$13));$skip(68); val res$14 =  // returns Array(int arg0) : Array[Parameter]
  methodM.getParameterTypes;System.out.println("""res14: Array[Class[_]] = """ + $show(res$14));$skip(50); val res$15 =  // returns Array(int) : Array[Class[_]]
  methodM.getReturnType;System.out.println("""res15: Class[?0] = """ + $show(res$15));$skip(75); val res$16 =  // returns int : Class[_]
  methodM.getTypeParameters;System.out.println("""res16: Array[java.lang.reflect.TypeVariable[java.lang.reflect.Method]] = """ + $show(res$16));$skip(37); val res$17 =  // returns Array():Array[TypeVariable[Method]]
  methodM.isVarArgs;System.out.println("""res17: Boolean = """ + $show(res$17));$skip(58); val res$18 =  // returns false
  methodM.invoke(c, new Integer(3));System.out.println("""res18: Object = """ + $show(res$18));$skip(56); val res$19 =  // returns 3 : Object

  classC.getDeclaredConstructors;System.out.println("""res19: Array[java.lang.reflect.Constructor[_]] = """ + $show(res$19));$skip(45);  // returns Array(C())
  val constructorC = classC.getConstructor();System.out.println("""constructorC  : java.lang.reflect.Constructor[tadp_reflection_scala.simple_reflection.C] = """ + $show(constructorC ));$skip(48); val res$20 = 
  constructorC.newInstance() // returns a new C;System.out.println("""res20: tadp_reflection_scala.simple_reflection.C = """ + $show(res$20))}
}
