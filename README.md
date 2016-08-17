# El compilador y usted

Uno de los features más interesantes que ofrece un lenguaje es la capacidad de extenderlo. En mayor o menor
medida, siempre que escribo un programa "extiendo" de alguna manera lo que ya existe, pero algunas
tecnologías ofrecen además herramientas para, sin necesariamente cambiar su sintaxis, manipular la forma de
escribirlas.

Este tipo de herramientas permite eliminar gran cantidad de boilerplate y, a veces, crear una
manera completamente distinta de expresar la solución para un problema. Esto tiene un impacto muy grande en
la legibilidad y extensibilidad del código y permite crear desde mejoras menores para evitar repetición o
simplificar la lectura, hasta DSLs internos o sistemas de reescritura que aprovechan la metadata del código
para trabajar en mejores niveles de abstracción.

Vamos a contar algunos ejemplos de estás ideas implementadas en el lenguaje Scala. Cómo usarlas, qué pasa
abajo del capó y en qué situaciones podrían ser útiles. 

# Case Classes

Una *Case Class* es una abstracción pensada para proveer una forma rápida y sintácticamente agradable para
crear estructuras inmutables similares a los *Tipos Algebraicos* de los lenguajes funcionales. Si bien son
un concepto interesante, lo más importante es entender que son solamente un "atajo". Scala no hace nada
especial con las case classes, simplemente cada definición "case" es pasada por un proceso de reescritura
en el compilador: Por cada Case Class se define una clase común y corriente, pero se implementan
automáticamente algunos métodos bien conocidos coyo código es predecible o repetitivo.

Entonces, una *Case Class* es simplemente *una clase cualquiera* al que el compilador le agrega:
- Properties de sólo lectura para cada parámetro de clase
- Una forma sencilla de clonarla (método copy)
- Una conversión a String clara, basada en su nombre y sus parámetros de clase
- Métodos de comparación y hashing
- Un companion object que puede usarse para construir instancias y como patrón para matching
- Otros detalles (las case clases de aridad >1 extienden Product) 

Sólo con este poquito las diferencias que se producen en el cógido son enormes. Acá hay dos
implementaciones de la misma(\*) clase, una usando *case classes* y una sin ellas.

###Con Case Classes
~~~scala
// Properties de sólo lectura
// Copy
// Conversión a String lindo
// Comparación / hashing
// Constructor sin new, parcialmente aplicable
// Unapply
// Etc...
case class Materia(nombre: String, ciclo: Int)(criterioDeAprobación: Alumno => Boolean) {
	def aprobados(curso: Curso) = curso filter criterioDeAprobación
}
~~~

###Sin Case Classes
~~~scala
class Materia {
	// Properties de sólo lectura
	private var _nombre: String = _
	def nombre = _nombre
	private var _ciclo: Int = _
	def ciclo = _ciclo
	private var _criterioDeAprobación: Alumno => Boolean = _
	private def criterioDeAprobación = _criterioDeAprobación

	private def this(nombre: String, ciclo: Int, criterioDeAprovación: Alumno => Boolean) {
		this()
		_nombre = nombre
		_ciclo = ciclo
		_criterioDeAprobación = criterioDeAprobación
	}

	// Copy
	def copy(nombre: String = this.nombre, ciclo: Int = this.ciclo) = new Materia(nombre,ciclo,criterioDeAprobación)
	
	// Conversión a String lindo
	override def toString = s"Materia($nombre, $ciclo)"

	// Comparación / hashing
	override def equals(other: Any) = other match {
		case m: Materia => m.nombre == nombre && m.ciclo == ciclo
		case _ => false
	}
	override def hashCode = ???

	def aprobados(curso: Curso) = curso filter criterioDeAprobación
}

object Materia {
	// Constructor sin new, parcialmente aplicable
	def apply(nombre: String, ciclo: Int) = { criterioDeAprobación: (Alumno => Boolean) =>
		new Materia(nombre, ciclo, criterioDeAprobación)
	}

	// Unapply
	def unapply(materia: Materia): Option[(String, Int)] = ???
}
~~~

## String Interpolators

Muchos lenguajes ofrecen la posibilidad de interpolar Strings para evitar el boilerplate y la confusión
producto de las cadenas larguisimas de concatenación necesarias para poder construir un string a partir de
multiples objetos.

La forma de hacer esto en scala es precediendo el string con una *s* y envolviendo las expresiones a
interpolar con ${...} (las expresiones que sólo consisten en una variable pueden prescindir de las llaves). 

~~~scala
val nombre = "Técnicas Avanzadas de Programación"
val ciclo = 3
val alumnos: List[Alumno] = ...

val sinInterpolación = "La materia " + nombre + " del ciclo " + ciclo + " tiene " + alumnos.size + "alumnos"

val conInterpolación = s"La materia $nombre del ciclo $ciclo tiene ${alumnos.size} alumnos"
~~~

La prueba de que esta forma de escritura es (al menos ligeramente) mejor que la concatenación directa está
en que la mayoría de la gente no nota a simple vista que al último string del ejemplo le falta un espacio ;)

Además de la "s" Scala ofrece otros interpoladores:

~~~scala
// f: Permite preceder las expresiones insertadas por un patron de formateo. Y es type safe!
val formateado = f"El promedio en $nombre%s es ${alumnos.sum / alumnos.size}%2.2f"

// raw: Trata a los caracteres especiales que modificarían el string como caracteres normales. 
val procesado   = s"Estos\nSon\nSaltos\nDe\nLinea"
val sinProcesar = raw"Estos\nNo\nSon\nSaltos\nDe\nLinea"
~~~

Sin embargo, el aspecto más interesante de la interpolación de strings en Scala es que *no son palabras
reservadas, sino mensajes*. Tanto *s* y *f* como *raw* son en realidad mensajes que el compilador envía a
una instancia de *StringContext* cuando ve el string literal. Esto permite que creemos nuestros propios
interpoladores extendiendo StringContext!

Digamos que tenemos una clase con la que representamos los mils y nos gustaría poder mostrar una lista de
mails mostrando sólo las primeras 4 letras y el dominio de cada uno (para evitar los crawlers). Una forma
posible para hacer esto es definir un interpolador que procese los parametros del tipo Email de forma
distinta a los demás:

~~~scala
class Email(val id: String, val dominio: String) {
	override def toString = s"$id@$dominio"
	def toEncriptedString = s"${id.take(4)}...@$dominio"
}

implicit class EmailsContext(val context: StringContext) {
	def emails(arguments: Any*) = ("" /: context.parts.zip(arguments)) {
		case (acum, (part, exp: Email)) => acum + part + exp.toEncriptedString
		case (acum, (part, exp))        => acum + part + exp
	}
}

val nico = new Email("nscarcella", "gmail.com")
val ernesto = new Email("bossi.ernestog", "gmail.com")
val ivan = new Email("ivanlocolosredoooo1964", "hotmail.com.es")

emails"$nico, $ernesto, $ivan" // Esto va a mostrar los mails encriptados
s"$nico, $ernesto, $ivan" // Esto va a mostrar los mails completos
~~~

Este es sólo un pequeño ejemplo de lo que se puede hacer con interpoladores. Noten que no hay ninguna
necesidad de que el método que se envía a un StringContext sea un String; esto quiere decir que podemos
usarlos para construir todo tipo de objetos a partir de Strings!

~~~scala
implicit class EmailContext(val context: StringContext) {
	def mail(arguments: Any*): Email = {
		val mergeado = context.s(arguments:_*)
		val extractor = "(.*)@(.*)".r
		val extractor(id,dominio) = mergeado
		
		new Email(id,dominio)			
	}
}

mail"un-id@un-dominio.com" // retorna un Email
mail"lalala" // falla por no cumplir el patrón
~~~

Todo muy lindo pero cómo llegan esos métodos a StringContext? y que es esa palabra "implicit"? Bueno, eso
nos lleva al siguiente tema...

## Implicits

Los implicits son una de las herramientas más novedosas y mágicas de Scala. Vienen en varios sabores, pero
lo que todos ellos tienen en común es que permiten poner cosas en un contexto para luego poder usarlas sin
una referencia explicita en el código (o sea, implicitamente :P).

### Implicit Class

Una *Clase Implicita* es una forma declarativa y no-invasiva de extender una clase.

Digamos que buscamos agregarle comportamiento a un objeto *sin cambiar su implementación*. Una forma
conocida de hacer esto es anteponiendo otro objeto que sepa hacer el nuevo comportamiento y tenga una
referencia al objeto viejo para poder usar el comportamiento ya existente.

~~~scala
class StringExtendido(unString: String) {
	// Este método es demasiado específico para querer ponerlo en String
	def esUnMail = unString.length > 10 && unString.contains("@") && unString.endsWith(".com") 
}
  
new StringExtendido("foobar@gmail.com").esUnMail // Sí!
new StringExtendido("Hola Mundo!").esUnMail      // No!
"Chau Mundo...".esUnMail                         // Esto no compila. No cambié la clase String.
~~~

La ventaja de esta aproximación es que puedo agregar tantos métodos cómo quiero sin preocuparme de que
colisionen diferentes implementaciones, ya que cada uno puede instanciar el "wrapper" de String que
prefiere; lo malo es que ensuciar el código para envolver al String.

Pero qué tal si pudiera explicarle al compilador lo que estoy tratando de hacer? Qué tal si pudiera pedirle
que, cuando vea que estoy mandandole a un String un mensaje que no entiende, que se fije a ver si está
definido en esta clase wrapper y, si está, que en vez de fallar me lo envuelva él solito?

Bueno, eso es exactamente lo que son las *Clases Implicitas*: Una clase común y corriente que, cuando están
en contexto, el compilador usa para wrapear objetos que no hubieran entendido algún mensaje.

Para definir una clase implicita, alcanza con hacerle recibir un único parámetro de clase del tipo que
quiero wrappear y anteponer la palabra "implicit" para que el compilador sepa que puede usarla:  

~~~scala
implicit class StringExtendido(unString: String) {
	def esUnMail = unString.length > 10 && unString.contains("@") && unString.endsWith(".com") 
}
  
"foobar@gmail.com".esUnMail // Ahora esto funciona! El compilador wrappea el string sin que yo lo vea.
new StringExtendido("foobar@gmail.com").esUnMail // La linea de arriba se reescribe a esto. 
~~~

Es importante notar que el wrappeo automático sólo ocurre cuando mando un mensaje *que el objeto no
entiende*. Esto significa que las clases implicitas sirven para extender un tipo, pero no para redefinirle
implementaciones. Esto las convierte en una forma segura de extender una interfaz, sin preocuparse por
romper la implementación previa. Este es un buen momento para mirar el código que usamos para extender StringContext en
el tema anterior y asegurarse de entender que está pasando.

También hay que saber que el compilador no va a tratar de encadenar más de una aplicación de implicits por
expresión, así que hay que cuidar las firmas... 

## Implicit Methods (Implicit Conversions)

Las conversiones implicitas son similares a las clases implicitas pero, en lugar de definir una nueva clase para extender
una referencia, se utiliza para convertir algo de un tipo a otro ya existente.
 
Digamos, por ejemplo, que tenemos un sistema con las siguientes interfaces::

~~~scala
class Punto(x: Int, y: Int)
object Mapa { def nombreDelLugar(lugar: Punto): String = ??? }
object Input { def puntoPresionado: (Int, Int) = ??? }
 
// Pedirle al mapa el nombre del punto presionado 
val lugar = Input.puntoPresionado

Mapa nombreDelLugar Input.puntoPresionado // Sería lindo poder hacerlo así, pero una tupla no es un punto...

Mapa.nombreDelLugar(new Punto(lugar._1, lugar._2)) // Hay que hacer una conversión
~~~

Algo incomodo en este código es que, al pedir el punto presionado, recibo una Tupla2 pero lo que necesito es un Punto.
Semánticamente no es un problema, dado que tengo una forma concreta de convertir cualquier Tupla2 en un Punto. Podría
incluso evitar una posible repetición de esta lógica extrayendo esa conversión en una función:

~~~scala
def tuplaAPunto(lugar: (Int,Int)) = new Punto(lugar._1, lugar._2)

Mapa.nombreDelLugar(tuplaAPunto(lugar)) // Ahora puedo usarlo así
~~~

Mejor? Sí. Pero si siempre que tengo una tupla y espero un punto tengo que aplicar esta función, sería lindo poder pedirle
al compilador que lo haga sin que yo lo tenga que escribir explicitamente; después de todo, mi función *tuplaAPunto* es
una transformación de Tuplas a Puntos.

Esto es exactamente para lo que las conversiones implicitas sirven. Puedo convertir una función que espera un único
parámetro en una conversión implicita anteponiendo la palabra implicit a su definición:

~~~scala
implicit def tuplaAPunto(lugar: (Int,Int)) = new Punto(lugar._1, lugar._2)
~~~

De ahora en adelante, si esta función (que es del tipo Tupla2 => Punto) está en contexto, el compilador va a aplicarla
automáticamente cada vez que usemos una Tupla2 en un lugar donde se esperaba un Punto. Eso permite reescribir nuestro uso
así:

~~~scala
Mapa.nombreDelLugar(Input.puntoPresionado)
~~~

Otro detalle interesante es que, por la forma en que Scala busca estos implicits, es posible, en lugar de importar la
función implicita en el contexto, definirla en el companion object de uno de los tipos en cuestión.

~~~scala
object Punto {
	implicit def tuplaAPunto(lugar: (Int,Int)) = new Punto(lugar._1, lugar._2)
}

Mapa.nombreDelLugar(Input.puntoPresionado) // No necesito importar la función!
~~~
De esta manera estamos oficializando que un Punto puede ser obtenido a partir de una tupla en cualquier lugar.  

### Implicit parámeters

Los parámetros implícitos permiten establecer un valor por defecto para un parámetro, que puede ser configurado para cada
contexto. Si existe al momento de evaluar una función un valor implicito del tipo de uno de sus parámetros implicitos,
este valor se usa como parámetro automáticamente sin necesidad de escribirlo. Esto es especialmente útil cuando existen
una gran cantidad de llamadas a funciones que usan el mismo parámetro en un contexto:

~~~scala
	class Persona { def persistir(db: BaseDeDatos) = ??? }
		
	class Familia(padre: Persona, madre: Persona, hijos: List[Persona], abuelos: List[Persona]) {
		def persistir(db: BaseDeDatos) {
			padre.persistir(db)
			madre.persistir(db)
			hijos.foreach(_.persistir(db))
			abuelos.foreach(_.persistir(db))
		}
	}
	
	\\ ...
	
	val db: BaseDeDatos = ???
	unaFamilia.persistir(db)
~~~

En este ejemplo, la base de datos del método persistir puede declararse como un parámetro implicito anteponiendo la
palabra *implicit* al nombre del parámetro. Cabe aclarar que los parámetros implicitos deben ser los últimos parámetros
de la firma y deben estar en su propio grupo de aplicación. 

~~~scala
	class Persona { def persistir(implicit db: BaseDeDatos) = ??? }
		
	class Familia(padre: Persona, madre: Persona, hijos: List[Persona], abuelos: List[Persona]) {
		def persistir(implicit db: BaseDeDatos) {
			padre.persistir
			madre.persistir
			hijos.foreach(_.persistir)
			abuelos.foreach(_.persistir)
		}
	}
	
	\\ ...
	
	implicit val db: BaseDeDatos = ???
	unaFamilia.persistir
~~~

Noten que, para poder evitar pasar el parámetro, es necesario que haya un *valor implicito* en el contexo. Los parámetros
implicitos son, a su vez, valores implicitos en el contexto del método.  

### Type Clases

Supongamos que tenemos un sistema que requiere poder persistir nuestros objetos de dominio utilizando multiples bases de
datos. Para mantener los ejemplos cortos, vamos a simplificar el problema suponiendo que solamente hace falta poder
guardar objetos (sin volver a leerlos o cambiarlos después).

Digamos que contamos con las siguientes interfaces para nuestras bases de datos:

~~~scala
	object SQL { def run(query: String) = ??? }
	object Redis { def guardar(clave: String, valor: String) = ??? }
~~~ 

#### Aproximación Naíve

Una primera manera para integrar nuestro código a estas APIs es extendiendo la interfaz de nuestros objetos, agregando métodos para persistir en cada
tecnología.

~~~scala
// SQL --------------------------------------------------------------------------------------------

trait PersistibleConSQL {
	def tabla: String
	def valores: List[String]
}

def persistirConSQL(obj: PersistibleConSQL) = {
	SQL run s"INSERT INTO ${obj.tabla} VALUES ${obj.valores}"
}

// Redis ------------------------------------------------------------------------------------------

trait PersistibleConRedis {
	def clave: String
	def valor: String
}

def persistirConRedis(obj: PersistibleConRedis) = {
	Redis guardar (obj.clave, obj.valor)
}

// Dominio ----------------------------------------------------------------------------------------

// Nuestra clase de dominio
case class C(f1: String, f2: String) extends PersistibleConRedis with PersistibleConSQL {
	def tabla = "C"
	def valores = List(f1, f2)
	def clave = "C"
	def valor = s"{f1: $f1, f2: $f2}"
}

// Uso --------------------------------------------------------------------------------------------

val c1 = new C("A", "1")
val c2 = new C("B", "2")
val c3 = new C("B", "3")

persistirConSQL(c1)
persistirConRedis(c2)
persistirConSQL(c3)
persistirConRedis(c3)
~~~ 

Esta aproximación tiene varios problemas: Es muy invasiva y requiere que todos las clases que se desean persistir puedan ser modificadas, ensucia la
interfaz de dominio y, si bien no es el caso en este ejemplo, podrían haber conflictos entre las interfaces requeridas por cada base de datos.

#### Aproximación Funcional

Lenguajes como Haskell proponen una aproximación alternativa: *Type Classes*.

Una Type Class define un conjunto de operaciones que tienen que ser soportadas por un tipo para pertenecer a ella. Cualquier tipo T puede pertenecer a la
type class C si alguien provee las operaciones que ella exige.

Guiandonos por esta idea, podemos definir una Type Class "PersistibleConSQL" que especifique todo lo que un Tipo debe poder hacer para ser considerado
persistible usando SQL:

~~~scala
trait PersistibleConSQL[T] {
	def tabla(obj: T): String
	def valores(obj: T): List[String]
}
~~~

Es importante notar que este trait no pretende ser *extendido* por los tipos persistibles con SQL, simplemente dice qué debe ser posible hacer con sus
instancias (en este caso, obtener una tabla y una lista de valores). Usando este trait, podemos reescribir nuestra función de persisitencia de la
siguiente forma:

~~~scala
def persistirConSQL[T](obj: T)(persistible: PersistibleConSQL[T]) = {
	SQL run s"INSERT INTO ${persistible.tabla(obj)} VALUES ${persistible.valores(obj)}"
}
~~~

La función persistir ahora trabaja con dos parámetros: Uno de ellos es una instancia del tipo T, el cual queremos persistir. El otro parámetro debe ser
alguien que implemente las funciones tabla y valores sobre T. Este objeto es el que permite que T sea persistible con SQL, permitiendo desacoplar la idea
de persistencia de nuestro tipo de dominio.

Ahora podemos reescribir nuestro código sin ensuciarlo con lógica de persistencia. A cambio, debemos implementar un objeto aparte que lo vuelva
persistible:

~~~scala
case class C(f1: String, f2: String)

object CSQL extends PersistibleConSQL[C] {
	def tabla(obj: C) = "C"
	def valores(obj: C) = List(obj.f1, obj.f2)
}

// uso

persistirConSQL(new C("foo", "bar"))(CSQL)
~~~

Podemos pensar que el trait PersistibleConSQL[T] es una Type Class y nuestro tipo de dominio C la implementa a travéz del objeto CSQL.

#### Mejorando el uso con implicits

El problema de esta solución es que ahora tengo que preocuparme por tener en contexto al objeto CSQL y pasarlo por parámetro cada vez que quiero usarlo.
Ahí es donde los parámetros implicitos hacen lo suyo! Si cambiamos nuestra función de persistencia para que el parametro que provee las funciones sea
implicito podemos dejar que el compilador lo escriba por nosotros.

~~~scala
def persistirConSQL[T](obj: T)(implicit persistible: PersistibleConSQL[T]) = {
	SQL run s"INSERT INTO ${persistible.tabla(obj)} VALUES ${persistible.valores(obj)}"
}

implicit object CSQL extends PersistibleConSQL[C] {
	def tabla(obj: C) = "C"
	def valores(obj: C) = List(obj.f1, obj.f2)
}

// uso

persistirConSQL(new C("foo", "bar"))

~~~

Esta forma de "extender" tipos es tan común y flexible que Scala provee una notación especial para definir funciones con type clases:

~~~scala
def persistirConSQL[T](obj: T)(implicit persistible: PersistibleConSQL[T]) { ??? } // Esto mismo puede escribirse como está en la linea de abajo  
def persistirConSQL[T : PersistibleConSQL](obj: T) { ??? } // Acá se ve más claro que esperamos un tipo T que pertenece a la typeclass PersistibleConSQL 
~~~ 

Uh... Pero ahora ya no está el parámetro "persistible"! Cómo consigo la instancia? Scala provee una función llamada *implicitly* para estas situaciones:
~~~scala
def persistirConSQL[T: PersistibleConSQL](obj: T) = {
	val persistible - implicitly[PersistibleConSQL[T]]
	SQL run s"INSERT INTO ${persistible.tabla(obj)} VALUES ${persistible.valores(obj)}"
}
~~~


## Macros

### Qué son?

Macros es una herramienta muy poderosa que permite definir reescrituras de AST (Abstract Syntax Tree) y está presente en
muchos lenguajes y tecnologías. A grandes razgos, la útilidad de las macros consiste en tomar una construcción sintáctica
válida y reemplazarla por otra en tiempo de compilación, permitiendo así que la sintaxis que normalmente construiría un
cierto programa construya otro totalmente diferente.

En Scala, la utilización de macros está definida en el paquete ~~~scala.language.experimental.macros~~~, el cual debe ser
importado para poder trabajar.

Una macro de Scala se compone de dos partes: Una declaración y una implementación. Al momento de compilar, los usos de la
función declaración son procesados para reemplazarlos por el resultado de aplicar la función implementación. Definir la
declaración de una macro es muy similar a definir una función común pero, en lugar del cuerpo, se utiliza la palabra clave
*macro* seguida del nombre de la función implementación. 

~~~scala
  // declaración
  def miMacro(parametro1: String, parametro2: Int) = macro miMacro_impl
  
  // implementación
  def miMacro_impl(c: Context)(parametro1: c.Expr[String], parametro2: c.Expr[Int]) = ???
~~~

### Usos

Veremos que hay varias maneras de colgarse del proceso de compilador, por lo que tenemos distintos tipos de macros propuestos por scala, solo que en este caso nos estaremos enfocando en uno de los tipos de macros. Otra consideración a tener en cuenta es que la interfaz que tenemos de macros como la de reflection en scala puede ir variando en el tiempo, ya que son aún implementaciones experimentales y no se ha llegado a un estado final de como sería la implementación definitiva.

Las macros han sido utilizados durante la versión 2.10 de Scala, tanto para aplicaciones de investigación como industriales, y la conclusión según [1], es que las macros han sido útiles para aplicaciones tales como:

- Code Generation
- Implementation of DSLs
- Static checking among others


### Tipos

Existen 2 tipos de macros, las llamadas "de caja blanca" o *whitebox* y las "de caja negra" o *blackbox*. La diferencia
entre los dos enfoques es que las macros de caja negra se usan cuando puedo definir claramente una firma para la función
que quiero implementar usando macros, mientras que las de caja blanca se usan cuando no puedo definir dicha firma. Las
macros de caja blanca son más flexibles pero menos seguras, ya que no pueden tiparse y van a ser discontinuadas en
versiones futuras de Scala, por esa razón vamos a concentrarnos en las definiciones de caja negra.

Para elegir uno de estos dos enfoques es necesario importar el paquete correspondiente ~~~scala.reflect.macros.whitebox~~~
para las de caja blanca y ~~~import scala.reflect.macros.blackbox~~~ para las de caja negra.

La clase Context que se usó en el código anterior está definida en estos paquetes.

Mirando el ejemplo, se puede ver que hay una relación entre el tipo de la declaración de la macro y su implementación que,
además de recibir un parámetro Context, espera también un parámetro por cada parámetro de la declaración que debe tener
el mismo nombre y un tipo de expresión que coincida. creado a partir del contexto. 

### Un primer ejemplo

Empecemos por hacer una macros sencilla: la función identidad, que recibe un número y lo retorna:

~~~scala
def id(n: Int): Int = macro id_impl

def id_impl(c: Context)(n: c.Expr[Int]): c.Expr[Int] = n
~~~

No fue tan difícil, no? Nuestra función id espera un Int y retorna un Int, por lo tanto, la macro con la que la
implementamos debe recibir (además del contexto) una expresión de tipo Int y retornar esa misma expresión. Es importante
notar que una expresión de tipo Int *no es* un Int, sino un fragmento de AST que, de ser evaluado, daría como resultado
un Int. Que sería el contexto en este caso y porque existe? Antes de eso vamos a explicar un poco algunos conceptos de lo que vimos recién. En el ejemplo que vimos el mismo se denominan def macros, y son métodos cuyas llamadas se expanden en tiempo de compilación, y con expansión se refiere en macros, a la transformación a código pero a nivel de compilación (no a al texto sintáctico ni al bytecode ejecutable, sino una representación intermedia de este) derivado del método al que se esta llamando con sus argumentos. El contexto se refiere al mismo en el cual se expone el código que será expandido y las rutinas que definimos que manipulan el código que deseemos transformar.

Otra parte del contexto de la macro es la funcion macroApplication, que nos permite proveer acceso al árbol de la expansión de la macro, y a pesar de que este arbol puede ser encontrado en argumentos de la implementación de la macro y en el método prefix, macroApplication nos permite dar un panorama más completo del contexto de la macro.

Veamos un ejemplo un poco más completo, por ejemplo implementemos un assert con macros...

~~~scala 
  def assert(contidion: Boolean, msg: String): Unit = macro assert_impl

  def assert_impl(c: Context)(contidion: c.Expr[Boolean], msg: c.Expr[String]) = {
    import c.universe._
  
    val q"assert ($condition, $msg)" = c.macroApplication
    q"if (!$condition) raise($msg)"
  }
~~~

El ejemplo tiene un par de cosas nuevas, por un lado vemos un q seguido de un string con signos de $ refiriendose a variables, para empezar q es básicamente un método que nos permite, mediante un string interpolator al cual podemos referinos a parámetros o valores dentro de la implementación de la macro, crear y hacer pattern matching código que podemos generar o transformar. En este caso lo que se esta haciendo es pattern matchear todo el contexto que recibimos del assert a dos variables y luego genera un código condicional, en otras palabras si llamamos a 

~~~scala 
assert("1 == 1", "Uno no es igual a uno")
~~~

este código se reemplaza en tiempo de compilación cuando la macro se invoke y realice la expansión a 

~~~scala 
if(! 1 == 1) raise("Uno no es igual a uno")
~~~

en la próxima sección veremos un poco más de lo que es este método q que permite que podamos crear estructuras mediante string interpolation.

### Quasiquotes

Implementar macros más complejas implica manipular el AST que conforma las expresiones. Esto es un trabajo muy pesado,
ya que requiere entender qué tipo de nodo se obtiene de cada posible expresión y como puede combinarse y deconstruirse
en base a otros.

Por suerte, las últimas versiones del framework de macros de Scala incluyen una herramienta muy poderosa para convertir
código a expresiones u obtener elementos a partir de un AST: *Los Quasiquotes*.

Quasiquotes son un tipo de StringContext que puede ser usado para aplicar y desaplicar valores desde/hacia un AST y
constituyen una interfaz relativamente accesible para manipular las expresiones.

El siguiente ejemplo usa quasiquotes para definir una macro que recibe una expresion por parámetro y loguea por consola
un aviso de que sentencia se va a ejecutar antes de evaluarla. 

~~~scala
def debug(code: => Unit): Unit = macro debug_impl

def debug_impl(c: Context)(code: c.Tree) = {
	import c.universe._

	val q"..$sentences" = code

	val loggedSentences = (sentences :\ List[c.Tree]()){
		case (sentence, acum) =>
			val msg = "Se va a ejecutar: " + showCode(sentence)
			val printSentence = q"println($msg)"

			printSentence :: sentence :: acum
	}

	q"..$loggedSentences"
}

//uso

val x = 10
debug {
  val x = 10         // Se va a ejecutar: val x = 10
  val y = 15         // Se va a ejecutar: val y = 15
  val z = x + y      // Se va a ejecutar: val z = x + y
}

~~~

### Validaciones y manejo de errores

El contexto de las macros permite realizar chequeos de tipos, validaciones sobre las expresiones e informar al compilador
de la necesidad de lanzar errores o warnings. Esto es un recurso excelente para validar construcciones estáticas en tiempo
de compilación.

El siguiente ejemplo retoma la idea de los mails con una macro que recibe un String y retorna un Email, pero falla si el
string no tiene el formato correcto. Este feedback puede verse en el mismo IDE, ya que se controla en tiempo de
compilación!

~~~scala
case class Email(id: String, domain: String)

def email(str: String): Email = macro email_impl
def email_impl(c: Context)(str: c.Expr[String]) = {
	import c.universe._

	val emailFormat = """(\w{4,})@([\w\.]+.com)""".r
	
	str match {
		case Expr(Literal(Constant(emailFormat(id, domain)))) =>	q"""Email($id,$domain)"""
		case _ => c.abort(c.enclosingPosition, "Formato inválido!!!")
	}
}

//uso
email("lalala@gmail.com") // Retorna Email("lalala", "gmail.com")
email("lalalagmail.com") // Error de compilación! Formato inválido!
~~~

## Poniendo todo junto

Podemos dar un último paso combinando un poco de todo lo que vimos. En el siguiente ejemplo se muestra cómo puede
extenderse StringContext mediante implicits para crear mails que son validados en tiempo de compilación. Si bien hay que
poner un poco de esfuerzo extra para adecuar las firmas y los usos, este es un gran ejemplo de lo poderosas que pueden
ser estas herramientas cuando se usan juntas. 

~~~scala
case class Email(id: String, domain: String)

implicit class EmailStringContext(strCtx: StringContext) {
	def email(arguments: Any*): Email = macro email_impl
}

def email_impl(c: Context)(arguments: c.Expr[Any]*) = {
	import c.universe._

	val emailFormat = """(\w{4,})@([\w\.]+.com)""".r
	
	c.prefix.tree match {
  		case Apply(_, List(Apply(_, rawParts))) =>
		  	val parts = rawParts map { case Literal(Constant(const: String)) => const }
		  	val args = arguments map { case Literal(Constant(const: String)) => const }
		  	val mail = ("" /: parts.zipAll(args, "", "")) {	case (acum, (part, arg)) => acum + part + arg	}
		
		  	mail match {
		  		case emailFormat(id,domain) => q"Email($id,$domain)"
		  		case _ => c.abort(c.enclosingPosition, "Invalid mail!!!")
		  	}
		case _ => c.abort(c.enclosingPosition, "Invalid mail!!!")
	}
}
~~~

Noten que no podemos pasarle a la macro el parámetro extra que el StringContext necesita, así que tenemos que obtenerlo
en base al contexto. 
 
## Referencias
- [http://docs.scala-lang.org/tutorials/tour/case-classes.html](http://docs.scala-lang.org/tutorials/tour/case-classes.html)
- [http://docs.scala-lang.org/overviews/core/string-interpolation.html](http://docs.scala-lang.org/overviews/core/string-interpolation.html)
- [http://docs.scala-lang.org/overviews/core/implicit-classes.html](http://docs.scala-lang.org/overviews/core/implicit-classes.html)
- [http://docs.scala-lang.org/tutorials/tour/implicit-conversions](http://docs.scala-lang.org/tutorials/tour/implicit-conversions)
- [http://docs.scala-lang.org/tutorials/tour/implicit-parameters.html](http://docs.scala-lang.org/tutorials/tour/implicit-parameters.html)
- [http://docs.scala-lang.org/tutorials/FAQ/finding-implicits.html](http://docs.scala-lang.org/tutorials/FAQ/finding-implicits.html)
- [http://docs.scala-lang.org/overviews/macros/overview.html](http://docs.scala-lang.org/overviews/macros/overview.html)
- [http://docs.scala-lang.org/overviews/macros/quasiquotes.html](http://docs.scala-lang.org/overviews/macros/quasiquotes.html)
