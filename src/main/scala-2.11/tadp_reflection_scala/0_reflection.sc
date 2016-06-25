package tadp_reflection_scala
import scala.reflect.runtime.universe._

object reflection_0 {

  classOf[Any]                                    //> res0: Class[Any](classOf[scala.Any]) = class java.lang.Object
  val classC = classOf[Reflection0.C]             //> classC  : Class[tadp_reflection_scala.Reflection0.C] = class tadp_reflection
                                                  //| _scala.Reflection0$C
  val c = new Reflection0.C                       //> c  : tadp_reflection_scala.Reflection0.C = tadp_reflection_scala.Reflection0
                                                  //| $C@1e643faf

	// returns "C" : String
  classC.getName                                  //> res1: String = tadp_reflection_scala.Reflection0$C
  
  // returns java.lang.Object : Class[_]
  classC.getSuperclass                            //> res2: Class[?0] = class java.lang.Object
  
  // returns Array(T) : Array[Class[_]]
  classC.getInterfaces                            //> res3: Array[Class[_]] = Array(interface tadp_reflection_scala.Reflection0$T)
                                                  //| 
  
  // returns false
  classC.isEnum                                   //> res4: Boolean = false
  
  // returns false
  //classC.isAnnotationPresent(classOf[SomeAnnotation])

	// returns Array(C.f) : Array[Field]
  classC.getDeclaredFields                        //> res5: Array[java.lang.reflect.Field] = Array(private int tadp_reflection_sca
                                                  //| la.Reflection0$C.f)
  
  // returns Array() ~> fields públicos (con heredados)
  classC.getFields                                //> res6: Array[java.lang.reflect.Field] = Array()
  
  val fieldF = classC.getDeclaredField("f")       //> fieldF  : java.lang.reflect.Field = private int tadp_reflection_scala.Reflec
                                                  //| tion0$C.f
  
  // returns int : Class[_]
  fieldF.getType                                  //> res7: Class[?0] = int
  
  // returns Array() : Array[Annotation]	fieldF.get(c) // Excepción! El campo es privado => no es accesible
  fieldF.getAnnotations                           //> res8: Array[java.lang.annotation.Annotation] = Array()
  fieldF.setAccessible(true)
  
  // returns 5
  fieldF.get(c)                                   //> res9: Object = 5
  fieldF.set(c, 8)
  // returns 8
  fieldF.get(c)                                   //> res10: Object = 8

	// returns Array(C.f_$eq(int), C.f(), C.m(int)) : Array[Method]
  classC.getDeclaredMethods                       //> res11: Array[java.lang.reflect.Method] = Array(public int tadp_reflection_s
                                                  //| cala.Reflection0$C.m(int), public int tadp_reflection_scala.Reflection0$C.f
                                                  //| (), public void tadp_reflection_scala.Reflection0$C.f_$eq(int))
  val methodM = classC.getMethod("m", classOf[Int])
                                                  //> methodM  : java.lang.reflect.Method = public int tadp_reflection_scala.Refl
                                                  //| ection0$C.m(int)
  
  // returns "m" : String
  methodM.getName                                 //> res12: String = m
  
  // returns Array(int arg0) : Array[Parameter]
  methodM.getParameters                           //> res13: Array[java.lang.reflect.Parameter] = Array(int arg0)
  
  // returns Array(int) : Array[Class[_]]
  methodM.getParameterTypes                       //> res14: Array[Class[_]] = Array(int)
  
  // returns int : Class[_]
  methodM.getReturnType                           //> res15: Class[?0] = int
  
  // returns Array():Array[TypeVariable[Method]]
  methodM.getTypeParameters                       //> res16: Array[java.lang.reflect.TypeVariable[java.lang.reflect.Method]] = Ar
                                                  //| ray()
  
  // returns false
  methodM.isVarArgs                               //> res17: Boolean = false
  
  // returns 3 : Object
  methodM.invoke(c, new Integer(3))               //> res18: Object = 3

	// returns Array(C())
  classC.getDeclaredConstructors                  //> res19: Array[java.lang.reflect.Constructor[_]] = Array(public tadp_reflecti
                                                  //| on_scala.Reflection0$C())
  val constructorC = classC.getConstructor()      //> constructorC  : java.lang.reflect.Constructor[tadp_reflection_scala.Reflect
                                                  //| ion0.C] = public tadp_reflection_scala.Reflection0$C()
  // returns a new C
  constructorC.newInstance()                      //> res20: tadp_reflection_scala.Reflection0.C = tadp_reflection_scala.Reflecti
                                                  //| on0$C@6bf256fa
}