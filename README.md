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

### Implicit parameters

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
