package tadp_reflection_scala
import scala.reflect.runtime.universe._

object type_tags_2 {
  def detectorDeEnteros(list: List[Any]) = list match {
    case list: List[Int] => true // WARNING! El [Int] no existe en runtime. Sólo List
    case other           => false
  }                                               //> detectorDeEnteros: (list: List[Any])Boolean

  detectorDeEnteros(List("no", "somos", "enteros"))
                                                  //> res0: Boolean = true

  // Usando el método typeTag
  val tt1 = typeTag[List[Int]]                    //> tt1  : reflect.runtime.universe.TypeTag[List[Int]] = TypeTag[List[Int]]

  // Usando un parámetro implicito. Si el compilador no encuentra un valor en contexto, lo genera.
  def obtenerTypeTag[T](implicit tt: TypeTag[T]) = tt
                                                  //> obtenerTypeTag: [T](implicit tt: reflect.runtime.universe.TypeTag[T])reflect
                                                  //| .runtime.universe.TypeTag[T]
  val tt2 = obtenerTypeTag[List[Int]]             //> tt2  : reflect.runtime.universe.TypeTag[List[Int]] = TypeTag[List[Int]]

  // Usando un Context Bound en un Type Parameter
  def obtenerTypeTagDeOtraForma[T: TypeTag] = implicitly[TypeTag[T]]
                                                  //> obtenerTypeTagDeOtraForma: [T](implicit evidence$1: reflect.runtime.universe
                                                  //| .TypeTag[T])reflect.runtime.universe.TypeTag[T]

  /*  def detectorDeEnteros[T: TypeTag](list: List[T]) =
    typeTag[T].tpe =:= typeOf[Int]

  detectorDeEnteros(List("no", "somos", "enteros")) // returns false
 */

	/*
  def explotar[T: TypeTag] = typeTag[T].tpe match {
    case TypeRef(typePrefix, symbol, typeArguments) => (typePrefix, symbol, typeArguments)
  }

  val (typePrefix, symbol, typeArguments) = explotar[List[Int]]

  typePrefix // package object scala : Symbol
  symbol // type List : Symbol
  typeArguments // List(Int) : List[Type]
  */
}