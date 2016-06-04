# TADP - 2015 C2 - Clase 09 - Pattern Matching vs Polimorfismo Ad-Hoc, Inmutabilidad

## Intro

Empezamos contando el patrón visitor[1] del libro de Gamma. La idea es que vean como se resuelve en objetos un problema en donde la solución depende de dos tipos para poder resolverlo.

Con esto podemos empezar a ver el problema actual (ejercicio Microprocesador), mostrando la solución en objetos basada en el patrón Visitor:  

[https://github.com/uqbar-paco/tadp-2015c2-clase9-microprocesador/tree/master/objetos-puro](https://github.com/uqbar-paco/tadp-2015c2-clase9-microprocesador/tree/master/objetos-puro)

Acá se puede apreciar que la solución en objetos es bastante complicada, a pesar de ser lo mejor que podemos hacer utilizando solamente polimorfismo ad-hoc. ¿cómo se puede mejorar entonces? Usando pattern matching.

Dado que es más probable que agreguemos más operaciones (como ejecutar, simplificar, pretty printing, etc) que instrucciones, es menos problemático si en vez de usar polimorfismo, miramos explícitamente la estructura. En definitiva es lo que se hacía antes con el visitor, solamente que ahora esto se define dentro de la operación directamente.

## Pattern Matching

Entonces, ¿qué es pattern matching? En principio es una forma de ejecutar distinto comportamiento dependiendo de la forma del objeto. Pero en vez de delegar esta decisión en el objeto, lo realiza el objeto que lo usa. Como vamos a ir viendo, “dependiendo de la forma del objeto” es más que simplemente ver de qué tipo es, sino también ver que cosas tiene dentro.

Pasamos a una nueva solución que usa Pattern Matching en vez del Visitor:

[https://github.com/uqbar-paco/tadp-2015c2-clase9-microprocesador/tree/master/funcional-mutable](https://github.com/uqbar-paco/tadp-2015c2-clase9-microprocesador/tree/master/funcional-mutable)

Veamos cómo queda la nueva implementación para ejecutar el programa. Esta nueva implementación también usa pattern matching sobre la lista de instrucciones para trabajarla recursivamente mediante el patrón cabeza y cola, y para los casos en los cuales la lista no es vacía, se decide cómo ejecutar la siguiente instrucción en base a su forma:

~~~scala
 class HaltException extends Exception

 def ejecutar(micro: Microprocesador, programa: Instruccion*) = {

        def ejecutarR(programa: List[Instruccion]): Unit =

          programa match {

          case Nil =>

          case HALT :: _ => throw new HaltException

          case siguiente :: restantes =>

                  siguiente match {

              case NOP =>

              case ADD => micro.guardar(micro.a + micro.b)

              case MUL => micro.guardar(micro.a * micro.b)

              case SWAP =>

                val temp = micro.a

                micro.a = micro.b

                micro.b = temp

              case LODV(valor) => micro.a = valor

              case LOD(direccion) =>

micro.a = micro.memoriaDeDatos(direccion)

              case STR(direccion) =>

micro.memoriaDeDatos(direccion) = micro.a

              case IFNZ(instrucciones @ _*) =>

                micro.pc += siguiente.bytes

                if (micro.a != 0) ejecutarR(instrucciones)

                else micro.pc += instrucciones.map(_.bytes).sum

            }

            micro.pc += siguiente.bytes

            ejecutarR(restantes)

        }

        try ejecutarR(programa.toList)

        catch {

          case e: HaltException =>

        }

  }
~~~

Para poder hacer esto, las instrucciones tienen que poder ser matcheadas con los patrones correspondientes. La forma más fácil de lograr esto (pero no la única) es definiendo las instrucciones como case classes/objects.

El case es un modificador que agrega de forma transparente comportamiento extra, y como el pattern matching en Scala se basa en entender ciertos mensajes, al decir que por ejemplo IFNZ es una case class hacemos que este tipo de instrucciones incorporen la implementación por defecto de dichos mensajes, entre otras cosas simpáticas como la igualdad y el hash code, y la forma de imprimirse, que se basan en los parámetros de clase.

Así quedan las instrucciones luego de este cambio:

~~~scala
abstract class Instruccion(val bytes: Int = 1)

case object NOP extends Instruccion

case object ADD extends Instruccion

case object MUL extends Instruccion

case object SWAP extends Instruccion

case object HALT extends Instruccion

case class LODV(valor: Short) extends Instruccion(2)

case class LOD(direccion: Int) extends Instruccion(3)

case class STR(direccion: Int) extends Instruccion(3)

case class IFNZ(instrucciones: Instruccion*) extends Instruccion

object END {

  val bytes = 1

}
~~~

Algo a tener en cuenta sobre los objetos sobre los cuales vamos a querer trabajar usando pattern matching, es que no deberían poder mutar aquellos valores sobre los cuales pretendemos matchear. Por ese motivo, los parámetros de clase de una case class son por defecto vals, podemos acceder al valor de forma pública pero no pueden ser modificados.

Vemos que con este nuevo enfoque, hacer una operación para simplificar un programa es muy sencillo, y lograr esta misma funcionalidad con el enfoque del visitor hubiera sido inviable por la complejidad extra de tener que analizar varios elementos consecutivos de la lista de instrucciones a la vez, con lo cual también el recorrido de la lista deja de ser trivial.

~~~scala
def simplificar(programa: Instruccion*): Seq[Instruccion] = {

    programa.toList match {

      case Nil => Nil

      case NOP :: restantes => simplificar(restantes: _*)

      case SWAP :: SWAP :: restantes => simplificar(restantes: _*)  

      case LODV(_) :: LODV(y) :: restantes =>

            simplificar ( LODV(y) :: restantes: _*)

      case LOD(_) :: LOD(y) :: restantes =>

            simplificar ( LOD(y) :: restantes: _*)

      case STR(_) :: STR(y) :: restantes =>

            simplificar ( STR(y) :: restantes: _*)

      case IFNZ() :: restantes => simplificar(restantes: _*)

      case IFNZ(instrucciones @ _*) :: restantes =>

              val r = simplificar(instrucciones: _*)

              if(r.isEmpty) simplificar(restantes: _*)

              else IFNZ(r: _*) :: simplificar( restantes: _*)

      case siguiente :: restantes =>

            siguiente :: simplificar(restantes : _*)

    }

}
~~~

## Inmutabilidad

Ya en el paso anterior nos empezamos a meter con la idea de inmutabilidad porque el pattern matching se para sobre estructuras inmutables, pero nuestra solución todavía trabaja con efecto colateral sobre el microprocesador.

¿Por qué queremos hacer el micro inmutable? En principio, un valor es más sencillo de manejar que un objeto. Al no haber mutabilidad, una operación es siempre predecible y podría simplificar el debuggeo y testeo de la solución. Además, esto nos va a permitir más adelante utilizar otros conceptos de funcional (como mónadas), pero por ahora esa parte nos la tienen que creer.

Además de hacer el micro inmutable, hay que tener en cuenta el manejo de errores. Actualmente se está usando una excepción para manejar la instrucción HALT. Esto tiene un efecto que es modificar el stack, lo cual rompe un poco con la idea de la transparencia referencial que es un concepto fundamental para el paradigma funcional.

Entonces debemos hacer al menos 3 cosas para acercarnos más a una solución funcionalosa:

- Hacer que el micro sea inmutable e ir generando nuevas copias del micro a medida que se ejecuta
- Reemplazar las excepciones por algo que represente la ejecución del programa (ejecutando, terminado, error)
- Incluir toda esa información en el valor de retorno de la ejecución en vez de tener un valor de retorno de tipo Unit

Para hacer el punto 1, es conveniente hacer del micro una case class, sólo por el hecho de que Scala ya nos provee un copy que es útil para no tener que ir instanciando todo de nuevo (es un chiche, pero es mucho más cómodo que hacer el new del micro y pasarle siempre TODAS las cosas).

~~~scala
case class Microprocesador(memoriaDeDatos: List[Short] =

  (1 to 1024).map(i => 0:Short), a: Short = 0, b: Short = 0,

  pc: Int = 0)

{

  def pc_+=(inc: Int) = copy(pc = pc + inc)

  def guardar(valor: Int) = copy(

        a = ((valor & 0xFF00) >> 4).toShort,

        b = (valor & 0x00FF).toShort

  )

}
~~~

Para el punto 2 hay que tener en cuenta 2 cosas: por un lado este resultado es el que va a ir llevando el estado durante toda la ejecución, así que parte de su responsabilidad va a ser esa. Y por otro lado, ¡por esta clase nomás!, vamos a tener que manejar este retorno dentro de la ejecución para saber si seguir ejecutando o no (más pattern matching), por eso también hacemos que sean case classes.

~~~scala
trait ResultadoDeEjecucion

case class Ejecutando(micro: Microprocesador) extends ResultadoDeEjecucion

case class Halt(micro: Microprocesador) extends ResultadoDeEjecucion

case class Error(micro: Microprocesador, descripcion: String) extends ResultadoDeEjecucion
~~~

Finalmente para el punto 3, así nos queda la ejecución retornando los resultados de ejecución:

~~~scala
def ejecutar(micro: Microprocesador, programa: Instruccion*): ResultadoDeEjecucion = programa.toList match {

case Nil => Ejecutando(micro)

case instruccionActual :: restantes =>

  val resultado = instruccionActual match {

    case HALT => Halt(micro)

    case IFNZ(instruccionesInternas @ _*) =>

      if (micro.a != 0)

            ejecutar(micro pc_+= instruccionActual.bytes,

              instruccionesInternas: _*) match {

                    case Ejecutando(m) => Ejecutando(m pc_+= END.bytes)

                    case otro => otro

              }

      else Ejecutando(micro pc_+= instruccionActual.bytes +

            instruccionesInternas.map(_.bytes).sum + END.bytes)

   

    case DIV =>

      if(micro.b == 0)

            Error(micro, "Division por 0.")

      Ejecutando(micro.guardar(micro.a/micro.b))

           

    case otra =>

      val siguienteMicro = otra match {

            case NOP => micro

            case ADD => micro.guardar(micro.a + micro.b)

            case MUL => micro.guardar(micro.a * micro.b)

            case SWAP => micro.copy(a = micro.b, b = micro.a)

            case LODV(valor) => micro.copy(a = valor)

            case LOD(direccion) =>

                    micro.copy(a = micro.memoriaDeDatos(direccion))

            case STR(direccion) =>

                    micro.copy(memoriaDeDatos = micro.memoriaDeDatos.updated(direccion, micro.a))

      }

      Ejecutando(siguienteMicro.pc_+=(otra.bytes))

  }

  resultado match {

    case Ejecutando(micro) => ejecutar(micro, restantes: _*)

    case x => x

  }

}
~~~

La clase que viene vamos a partir de esta solución que todavía no está tan copada, porque el manejo del resultado de ejecución es engorroso y más manual de lo que nos gustaría. Sólo quedémonos con la idea de que dimos un paso grande hacia la felicidad!

El código final luego de este paso se encuentra acá:

[https://github.com/uqbar-paco/tadp-2015c2-clase9-microprocesador/tree/master/funcional-inmutable](https://github.com/uqbar-paco/tadp-2015c2-clase9-microprocesador/tree/master/funcional-inmutable)

[1] [SourceMaking: Visitor](https://www.google.com/url?q=https://sourcemaking.com/design_patterns/visitor&sa=D&ust=1465054556955000&usg=AFQjCNF250rlMRq7KCXAtkNT9cwuhA_cxA)

