package tadp_reflection_scala.symbols
import scala.reflect.runtime.universe._

object symbols_methods_5 {

  val methodSymbol = typeOf[D[Int]].decl("m": TermName).asMethod

  // returns Type [U >: Int](x: Int)(y: U)Int
  methodSymbol.typeSignatureIn(typeOf[C[Int]])

  // returns Type [U >: String](x: Int)(y: U)Int
  methodSymbol.typeSignatureIn(typeOf[C[String]])

  // return m: methodSymbol.NameType
  methodSymbol.name

  // returns C: ClassSymbol
  methodSymbol.owner

  // returns true
  methodSymbol.isMethod

  // returns false
  methodSymbol.isConstructor

  //returns true
  methodSymbol.isPublic

  // returns List(List(value x), List(value y)) : List[List[Symbol]]
  methodSymbol.paramLists

}