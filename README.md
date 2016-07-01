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
```scala
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
```

###Sin Case Classes
```scala
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
```

## String Interpolators

Muchos lenguajes ofrecen la posibilidad de interpolar Strings para evitar el boilerplate y la confusión
producto de las cadenas larguisimas de concatenación necesarias para poder construir un string a partir de
multiples objetos.

La forma de hacer esto en scala es precediendo el string con una *s* y envolviendo las expresiones a
interpolar con ${...} (las expresiones que sólo consisten en una variable pueden prescindir de las llaves). 

```scala
val nombre = "Técnicas Avanzadas de Programación"
val ciclo = 3
val alumnos: List[Alumno] = ...

val sinInterpolación = "La materia " + nombre + " del ciclo " + ciclo + " tiene " + alumnos.size + "alumnos"

val conInterpolación = s"La materia $nombre del ciclo $ciclo tiene ${alumnos.size} alumnos"
```

La prueba de que esta forma de escritura es (al menos ligeramente) mejor que la concatenación directa está
en que la mayoría de la gente no nota a simple vista que al último string del ejemplo le falta un espacio ;)

Además de la "s" Scala ofrece otros interpoladores:

```scala
// f: Permite preceder las expresiones insertadas por un patron de formateo. Y es type safe!
val formateado = f"El promedio en $nombre%s es ${alumnos.sum / alumnos.size}%2.2f"

// raw: Trata a los caracteres especiales que modificarían el string como caracteres normales. 
val procesado   = s"Estos\nSon\nSaltos\nDe\nLinea"
val sinProcesar = raw"Estos\nNo\nSon\nSaltos\nDe\nLinea"
```

Sin embargo, el aspecto más interesante de la interpolación de strings en Scala es que *no son palabras
reservadas, sino mensajes*. Tanto *s* y *f* como *raw* son en realidad mensajes que el compilador envía a
una instancia de *StringContext* cuando ve el string literal. Esto permite que creemos nuestros propios
interpoladores extendiendo StringContext!

Digamos que tenemos una clase con la que representamos los mils y nos gustaría poder mostrar una lista de
mails mostrando sólo las primeras 4 letras y el dominio de cada uno (para evitar los crawlers). Una forma
posible para hacer esto es definir un interpolador que procese los parametros del tipo Email de forma
distinta a los demás:

```scala
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
```

Este es sólo un pequeño ejemplo de lo que se puede hacer con interpoladores. Noten que no hay ninguna
necesidad de que el método que se envía a un StringContext sea un String; esto quiere decir que podemos
usarlos para construir todo tipo de objetos a partir de Strings!

```scala
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
```

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

```scala
class StringExtendido(unString: String) {
	// Este método es demasiado específico para querer ponerlo en String
	def esUnMail = unString.length > 10 && unString.contains("@") && unString.endsWith(".com") 
}
  
new StringExtendido("foobar@gmail.com").esUnMail // Sí!
new StringExtendido("Hola Mundo!").esUnMail      // No!
"Chau Mundo...".esUnMail                         // Esto no compila. No cambié la clase String.
```

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

```scala
implicit class StringExtendido(unString: String) {
	def esUnMail = unString.length > 10 && unString.contains("@") && unString.endsWith(".com") 
}
  
"foobar@gmail.com".esUnMail // Ahora esto funciona! El compilador wrappea el string sin que yo lo vea.
new StringExtendido("foobar@gmail.com").esUnMail // La linea de arriba se reescribe a esto. 
```

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

```scala
class Punto(x: Int, y: Int)
object Mapa { def nombreDelLugar(lugar: Punto): String = ??? }
object Input { def puntoPresionado: (Int, Int) = ??? }
 
// Pedirle al mapa el nombre del punto presionado 
val lugar = Input.puntoPresionado

Mapa nombreDelLugar Input.puntoPresionado // Sería lindo poder hacerlo así, pero una tupla no es un punto...

Mapa.nombreDelLugar(new Punto(lugar._1, lugar._2)) // Hay que hacer una conversión
```

Algo incomodo en este código es que, al pedir el punto presionado, recibo una Tupla2 pero lo que necesito es un Punto.
Semánticamente no es un problema, dado que tengo una forma concreta de convertir cualquier Tupla2 en un Punto. Podría
incluso evitar una posible repetición de esta lógica extrayendo esa conversión en una función:

```scala
def tuplaAPunto(lugar: (Int,Int)) = new Punto(lugar._1, lugar._2)

Mapa.nombreDelLugar(tuplaAPunto(lugar)) // Ahora puedo usarlo así
```

Mejor? Sí. Pero si siempre que tengo una tupla y espero un punto tengo que aplicar esta función, sería lindo poder pedirle
al compilador que lo haga sin que yo lo tenga que escribir explicitamente; después de todo, mi función *tuplaAPunto* es
una transformación de Tuplas a Puntos.

Esto es exactamente para lo que las conversiones implicitas sirven. Puedo convertir una función que espera un único
parámetro en una conversión implicita anteponiendo la palabra implicit a su definición:

```scala
implicit def tuplaAPunto(lugar: (Int,Int)) = new Punto(lugar._1, lugar._2)
```

De ahora en adelante, si esta función (que es del tipo Tupla2 => Punto) está en contexto, el compilador va a aplicarla
automáticamente cada vez que usemos una Tupla2 en un lugar donde se esperaba un Punto. Eso permite reescribir nuestro uso
así:

```scala
Mapa.nombreDelLugar(Input.puntoPresionado)
```

Otro detalle interesante es que, por la forma en que Scala busca estos implicits, es posible, en lugar de importar la
función implicita en el contexto, definirla en el companion object de uno de los tipos en cuestión.

```scala
object Punto {
	implicit def tuplaAPunto(lugar: (Int,Int)) = new Punto(lugar._1, lugar._2)
}

Mapa.nombreDelLugar(Input.puntoPresionado) // No necesito importar la función!
```
De esta manera estamos oficializando que un Punto puede ser obtenido a partir de una tupla en cualquier lugar.  

### Implicit parámeters

**TODO:** Contar el ejemplo.
  
## Macros

### Qué son?

Macros es una herramienta muy poderosa que permite definir reescrituras de AST (Abstract Syntax Tree) y está presente en
muchos lenguajes y tecnologías. A grandes razgos, la útilidad de las macros consiste en tomar una construcción sintáctica
válida y reemplazarla por otra en tiempo de compilación, permitiendo así que la sintaxis que normalmente construiría un
cierto programa construya otro totalmente diferente.

En Scala, la utilización de macros está definida en el paquete ```scala.language.experimental.macros```, el cual debe ser
importado para poder trabajar.

Una macro de Scala se compone de dos partes: Una declaración y una implementación. Al momento de compilar, los usos de la
función declaración son procesados para reemplazarlos por el resultado de aplicar la función implementación. Definir la
declaración de una macro es muy similar a definir una función común pero, en lugar del cuerpo, se utiliza la palabra clave
*macro* seguida del nombre de la función implementación. 

```scala
  // declaración
  def miMacro(parametro1: String, parametro2: Int) = macro miMacro_impl
  
  // implementación
  def miMacro_impl(c: Context)(parametro1: c.Expr[String], parametro2: c.Expr[Int]) = ???
```

### Tipos

Existen 2 tipos de macros, las llamadas "de caja blanca" o *whitebox* y las "de caja negra" o *blackbox*. La diferencia
entre los dos enfoques es que las macros de caja negra se usan cuando puedo definir claramente una firma para la función
que quiero implementar usando macros, mientras que las de caja blanca se usan cuando no puedo definir dicha firma. Las
macros de caja blanca son más flexibles pero menos seguras, ya que no pueden tiparse y van a ser discontinuadas en
versiones futuras de Scala, por esa razón vamos a concentrarnos en las definiciones de caja negra.

Para elegir uno de estos dos enfoques es necesario importar el paquete correspondiente ```scala.reflect.macros.whitebox```
para las de caja blanca y ```import scala.reflect.macros.blackbox``` para las de caja negra.

La clase Context que se usó en el código anterior está definida en estos paquetes.

### Ejemplos
 


   Contar
     - tipos:
- whitebox
- blackbox
     - quasiquotes
     - ejemplos:
- debugger
- email

## Dynamics
 - ejemplo con macros y dynamics

 
### Referencias
- http://docs.scala-lang.org/tutorials/tour/case-classes.html
- http://docs.scala-lang.org/overviews/core/string-interpolation.html
- http://docs.scala-lang.org/overviews/core/implicit-classes.html
- http://docs.scala-lang.org/tutorials/tour/implicit-conversions
- http://docs.scala-lang.org/tutorials/tour/implicit-parameters.html
- http://docs.scala-lang.org/tutorials/FAQ/finding-implicits.html