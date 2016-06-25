package tadp_reflection_scala

object simple_reflection {

  trait T { def f: Int }
  class C extends T {
    def m(x: Int) = x
    var f = 5
  }

  classOf[Any]
  val classC = classOf[C]
  val c = new C

  classC.getName // returns "C" : String
  classC.getSuperclass // returns java.lang.Object : Class[_]
  classC.getInterfaces // returns Array(T) : Array[Class[_]]
  classC.isEnum // returns false
  //classC.isAnnotationPresent(classOf[SomeAnnotation]) // returns false

  classC.getDeclaredFields // returns Array(C.f) : Array[Field]
  classC.getFields // returns Array() ~> fields públicos (con heredados)
  val fieldF = classC.getDeclaredField("f")
  fieldF.getType // returns int : Class[_]
  fieldF.getAnnotations // returns Array() : Array[Annotation]	fieldF.get(c) // Excepción! El campo es privado => no es accesible
  fieldF.setAccessible(true)
  fieldF.get(c) // returns 5
  fieldF.set(c, 8)
  fieldF.get(c) // returns 8

  classC.getDeclaredMethods // returns Array(C.f_$eq(int), C.f(), C.m(int)) : Array[Method]
  val methodM = classC.getMethod("m", classOf[Int])
  methodM.getName // returns "m" : String
  methodM.getParameters // returns Array(int arg0) : Array[Parameter]
  methodM.getParameterTypes // returns Array(int) : Array[Class[_]]
  methodM.getReturnType // returns int : Class[_]
  methodM.getTypeParameters // returns Array():Array[TypeVariable[Method]]
  methodM.isVarArgs // returns false
  methodM.invoke(c, new Integer(3)) // returns 3 : Object

  classC.getDeclaredConstructors // returns Array(C())
  val constructorC = classC.getConstructor()
  constructorC.newInstance() // returns a new C
}