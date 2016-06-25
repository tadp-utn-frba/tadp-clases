package tadp_reflection_scala.symbols
import scala.reflect.runtime.universe._

object symbols_3 {

// member retorna una instancia de Symbol
val testMember: Symbol = typeOf[C[Int]].member(TermName("test"))
                                                  //> testMember  : reflect.runtime.universe.Symbol = method test

// como sabemos que es un método, podemos obtener un MethodSymbol que tiene una interfaz más rica
val testMethod: MethodSymbol = testMember.asMethod//> testMethod  : reflect.runtime.universe.MethodSymbol = method test

}