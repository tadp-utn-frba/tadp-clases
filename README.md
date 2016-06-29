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


## Otras Cosas para contar
     
- String interpolators
     
- Implicits
 Contar:
   - Qué son?
   - Por donde se buscan?
 - methods
 - classes
 - parameters
   - Implicitly
   - Type classes
  
- Macros
   Contar
     - tipos:
- whitebox
- blackbox
     - quasiquotes
     - ejemplos:
- debugger
- email

- dynamics
 - ejemplo con macros y dynamics

 
### Referencias
- http://docs.scala-lang.org/tutorials/tour/case-classes.html
- http://docs.scala-lang.org/overviews/core/string-interpolation.html