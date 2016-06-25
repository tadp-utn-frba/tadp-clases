package tadp_reflection_scala.symbols
import scala.reflect.runtime.universe._

object instance_mirror_7 {

  val ru = scala.reflect.runtime.universe         //> ru  : scala.reflect.api.JavaUniverse = scala.reflect.runtime.JavaUniverse@25
                                                  //| bfcafd
  val runtimeMirror = ru.runtimeMirror(getClass.getClassLoader)
                                                  //> runtimeMirror  : tadp_reflection_scala.symbols.instance_mirror_7.ru.Mirror =
                                                  //|  JavaMirror with sun.misc.Launcher$AppClassLoader@33909752 of type class sun
                                                  //| .misc.Launcher$AppClassLoader with classpath [file:/home/ernesto/devtools/sc
                                                  //| ala_ide_tadp/configuration/org.eclipse.osgi/319/0/.cp/target/lib/worksheet-r
                                                  //| untime-library.jar,file:/opt/jdk/jdk1.8.0_60/jre/lib/resources.jar,file:/opt
                                                  //| /jdk/jdk1.8.0_60/jre/lib/rt.jar,file:/opt/jdk/jdk1.8.0_60/jre/lib/jsse.jar,f
                                                  //| ile:/opt/jdk/jdk1.8.0_60/jre/lib/jce.jar,file:/opt/jdk/jdk1.8.0_60/jre/lib/c
                                                  //| harsets.jar,file:/opt/jdk/jdk1.8.0_60/jre/lib/jfr.jar,file:/opt/jdk/jdk1.8.0
                                                  //| _60/jre/lib/ext/zipfs.jar,file:/opt/jdk/jdk1.8.0_60/jre/lib/ext/dnsns.jar,fi
                                                  //| le:/opt/jdk/jdk1.8.0_60/jre/lib/ext/jaccess.jar,file:/opt/jdk/jdk1.8.0_60/jr
                                                  //| e/lib/ext/sunjce_provider.jar,file:/opt/jdk/jdk1.8.0_60/jre/lib/ext/localeda
                                                  //| ta.jar,file:/opt/jdk/jdk1.8.0_60/jre/lib/ext/jfxrt.jar,file:/opt/jdk/jdk1.8.
                                                  //| 0_60/jre/lib/ext/sunpkcs
                                                  //| Output exceeds cutoff limit.
  val instanceMirror = runtimeMirror.reflect(new E)
                                                  //> instanceMirror  : tadp_reflection_scala.symbols.instance_mirror_7.ru.Instanc
                                                  //| eMirror = instance mirror for tadp_reflection_scala.symbols.E@5cde6747

  val classSymbol = instanceMirror.symbol.asClass //> classSymbol  : tadp_reflection_scala.symbols.instance_mirror_7.ru.ClassSymbo
                                                  //| l = class E
  val classType = classSymbol.toType              //> classType  : tadp_reflection_scala.symbols.instance_mirror_7.ru.Type = tadp_
                                                  //| reflection_scala.symbols.E
  // The concrete runtime java class for the symbol
  val javaClass = runtimeMirror.runtimeClass(classSymbol)
                                                  //> javaClass  : Class[_] = class tadp_reflection_scala.symbols.E

  //Method mirrors

  val methodSymbol = ru.typeOf[E].declaration(ru.TermName("x")).asMethod
                                                  //> methodSymbol  : tadp_reflection_scala.symbols.instance_mirror_7.ru.MethodSym
                                                  //| bol = method x
  val methodMirror = instanceMirror.reflectMethod(methodSymbol)
                                                  //> methodMirror  : tadp_reflection_scala.symbols.instance_mirror_7.ru.MethodMir
                                                  //| ror = method mirror for def x: Int (bound to tadp_reflection_scala.symbols.E
                                                  //| @5cde6747)
  //returns 2
  methodMirror.apply()                            //> res0: Any = 2

  //Field Mirrors
  
	/*
  val fieldSymbol = ru.typeOf[C].decl(ru.TermName("x")).asTerm
  // returns false
  fieldSymbol.isVal
  // returns true
  fieldSymbol.isMethod
  */
  
  //Class Mirrors
  
  /*
	val classSymbol = ru.typeOf[C].typeSymbol.asClass
	val classMirror = runtimeMirror.reflectClass(classSymbol)
	val constructorSymbol = ru.typeOf[C].decl(ru.nme.CONSTRUCTOR).asMethod
	val constructorMirror: ru.MethodMirror = classMirror.reflectConstructor(constructorSymbol)
	constructorMirror.apply(2) // returns C(2)
  */
  
  //Module Mirrors
  /*
  val moduleSymbol = ru.typeOf[C.type].termSymbol.asModule
	val moduleMirror = runtimeMirror.reflectModule(moduleSymbol)
	moduleMirror.instance // returns C
  */
}