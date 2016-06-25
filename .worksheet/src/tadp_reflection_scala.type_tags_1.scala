package tadp_reflection_scala
import scala.reflect.runtime.universe._

object type_tags_1 {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(271); 
  def detectorDeEnteros(list: List[Any]) = list match {
    case list: List[Int] => true // WARNING! El [Int] no existe en runtime. Sólo List
    case other           => false
  };System.out.println("""detectorDeEnteros: (list: List[Any])Boolean""");$skip(53); val res$0 = 

  detectorDeEnteros(List("no", "somos", "enteros"));System.out.println("""res0: Boolean = """ + $show(res$0));$skip(62); 

  // Usando el método typeTag
  val tt1 = typeTag[List[Int]];System.out.println("""tt1  : reflect.runtime.universe.TypeTag[List[Int]] = """ + $show(tt1 ));$skip(154); 

  // Usando un parámetro implicito. Si el compilador no encuentra un valor en contexto, lo genera.
  def obtenerTypeTag[T](implicit tt: TypeTag[T]) = tt;System.out.println("""obtenerTypeTag: [T](implicit tt: reflect.runtime.universe.TypeTag[T])reflect.runtime.universe.TypeTag[T]""");$skip(38); 
  val tt2 = obtenerTypeTag[List[Int]];System.out.println("""tt2  : reflect.runtime.universe.TypeTag[List[Int]] = """ + $show(tt2 ));$skip(120); 

  // Usando un Context Bound en un Type Parameter
  def obtenerTypeTagDeOtraForma[T: TypeTag] = implicitly[TypeTag[T]];System.out.println("""obtenerTypeTagDeOtraForma: [T](implicit evidence$1: reflect.runtime.universe.TypeTag[T])reflect.runtime.universe.TypeTag[T]""");$skip(315); 

  /*  def detectorDeEnteros[T: TypeTag](list: List[T]) =
    typeTag[T].tpe =:= typeOf[Int]

  detectorDeEnteros(List("no", "somos", "enteros")) // returns false
 */

  def explotar[T: TypeTag] = typeTag[T].tpe match {
    case TypeRef(typePrefix, symbol, typeArguments) => (typePrefix, symbol, typeArguments)
  };System.out.println("""explotar: [T](implicit evidence$2: reflect.runtime.universe.TypeTag[T])(reflect.runtime.universe.Type, reflect.runtime.universe.Symbol, List[reflect.runtime.universe.Type])""");$skip(65); 

  val (typePrefix, symbol, typeArguments) = explotar[List[Int]];System.out.println("""typePrefix  : reflect.runtime.universe.Type = """ + $show(typePrefix ));System.out.println("""symbol  : reflect.runtime.universe.Symbol = """ + $show(symbol ));System.out.println("""typeArguments  : List[reflect.runtime.universe.Type] = """ + $show(typeArguments ));$skip(47); val res$1 = 

  typePrefix;System.out.println("""res1: reflect.runtime.universe.Type = """ + $show(res$1));$skip(31); val res$2 =  // package object scala : Symbol
  symbol;System.out.println("""res2: reflect.runtime.universe.Symbol = """ + $show(res$2));$skip(42); val res$3 =  // type List : Symbol
  typeArguments // List(Int) : List[Type];System.out.println("""res3: List[reflect.runtime.universe.Type] = """ + $show(res$3))}
}
