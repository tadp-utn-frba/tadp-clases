# Type Arguments y Varianza

## Introducción
Arrancamos con un repaso básico de tipos.

![](https://raw.githubusercontent.com/tadp-utn-frba/tadp-utn-frba.github.io/source/scripts/granja.png)

~~~scala
var animal: Animal = ???
var vaca: Vaca = ???

animal.come
vaca.ordeñate
~~~

Tengo eso. Con que se puede inicializar?

~~~scala
var animal: Animal = new Vaca // Ok! Una vaca es un animal
var vaca: Vaca = new Animal   // No! Un animal no es necesariamente una vaca

animal.come
vaca.ordeñate
~~~

## Type Arguments

Hasta acá el tipado cierra. Llevemoslo un paso más mostrando colecciones. Armemos un conjunto de animales y tratemos de filtrar los que están gordos.

~~~scala
var miColeccion: Set = Set(new Vaca, new Caballo, new Granero)

miColeccion.filter{unElemento =>
  unElemento.estaGordo // Cómo sé si los elementos entienden esto?
}
~~~

Vemos que en este ejemplo no alcanza con decir que algo es una colección: Me va a importar también el tipo de las cosas que contiene.

De hecho, si recordamos el tipo de filter en Haskell era:

~~~haskell
filter::[a]->(a->Bool)->[a]
~~~

Es la misma operación la que me pide saber que tipo de elementos tiene. De acá se deduce que cualquier tipo podría requerir de un “subtipado”, dependiendo de los mensajes que queremos que entienda.

Scala me deja definir tipos que llevan parámetros (otros tipos) para poder contemplar estos escenarios.

Agregando esto, mi código quedaría:

~~~scala
var animales: Set[Animal] = Set(new Vaca, new Oveja, new Granero) // no me deja pasar granero porque no es un animal

animales.filter{ unElemento =>
    unElemento.estaGordo
}
~~~

El típo del mensaje filter va a ser muy parecido al de Haskell:

~~~haskell
#Set[A] >> filter(criterio: A=>Bool):Set[A]
~~~

## Ejercicio - Parte 1
Tomemos el modelo presentado y pensemos cómo hacer para agregar:

- **Corral**: Un corral es un lugar donde se acomodan varios animales de la misma especie.
- **Pastores**: Un pastor puede, al recibir la orden, llevar a pastar a un conjunto de animales.
- **Lecheros**: Los lecheros, cuando se les pide, ordeñan a todas las vacas de un corral.

Si programamos todo bien, el siguiente programa debería andar:

~~~scala
val corralito = ???
val lechero = ???
val pastor = ???

pastor.pastorear(corralito.animales)
lechero.ordeñar(corralito)
~~~

Una primer implementación posible podría ser la siguiente:

~~~scala
class Lechero {
  def ordeñar(corral: Corral) = corral.animales.foreach(_.ordeñate)
}
	
class Pastor {
  def pastorear(animales: Set[Animal]) = animales.foreach(_.come)
}

class Corral(val animales: Set[Animal])

val corralito = new Corral(Set(new Vaca, new Vaca, new Vaca))
val lechero = new Lechero
val pastor = new Pastor

pastor.pastorear(corralito.animales) // Todo en orden!
lechero.ordeñar(corralito)           // Nop. Animal no entiende ordeñate
~~~

Ufa… El pastor funciona, pero el lechero tiene un problema de tipos. Nada le asegura que los animales que hay en el corral sean vacas, así que no se anima a ordeñar. ¿Cómo se puede hacer para que esto tipe?

Después de laburarlo un poco y meterle type arguments podemos llegar a algo así:

~~~scala
class Lechero {
  def ordeñar(corral: Corral[Vaca]) = corral.animales.foreach(_.ordeñate)
}
	
class Pastor {
  def pastorear(animales: Set[Animal]) = animales.foreach(_.come)
}

class Corral[T](val animales: Set[T])

val corralito = new Corral(Set(new Vaca, new Vaca, new Vaca))
val lechero = new Lechero
val pastor = new Pastor

pastor.pastorear(corralito.animales) // Ups… Esto se rompio??? PORQUÉ???
lechero.ordeñar(corralito)           // Ahora sí! Esto anda!
~~~

Ok, el type argument en el Corral permite que el lechero sepa qué bicho está ordeñando! Incluso podemos hacer que el corral SOLO acepte animales haciendo:

~~~scala
class Corral[T <: Animal](val animales: Set[T])
~~~

Pero porqué ya no anda el pastor? Cómo que un Set de vacas no es un Set de animales???

## Varianza
Para entender el problema, simplifiquemos la situación y pensemos en los tipos...

~~~scala
var vacas: Set[Vaca] = ???
var animales: Set[Animal] = ???

animales.foreach{ animal => animal.come }
vacas.foreach{ vaca => vaca.ordeñate }
~~~

Entonces, vale inicializarlo con esto?

~~~scala
var vacas: Set[Vaca] = new Set[Vaca]()
var animales: Set[Animal] = new Set[Vaca]()

animales.foreach{ animal => animal.come }
vacas.foreach{ vaca => vaca.ordeñate }
~~~

En principio podría parecer que sí, pero vamos a ver que no es tan simple…

Qué pasa si cambio el código de esta forma:

~~~scala
var vacas: Set[Vaca] = new Set[Vaca]()
var animales: Set[Animal] = vacas

animales.add(new Caballo) // Opa! Un caballo es un animal, así que esto vale
vacas.foreach{ vaca => vaca.ordeñate } //Eh… No.
~~~

Entonces la respuesta es NO. Un Set[Vaca] no es un Set[Animales].

La forma en la que varía el subtipado de un tipo compuesto en relación a sus parámetros de tipo se denomina **Varianza**.

En el caso de Set[T], el tipo no acepta nada que no sea el T exacto declarado. Esta situación se denomina **Invarianza**.

---------------

Veamos un ejemplo de algo parecido:

~~~scala
var f : Vaca => Vaca

def g(vaca: Vaca): Vaca = … // Recibe una Vaca y devuelve una Vaca
def h(vaca: Vaca): Animal = … // Devuelve un Animal
def i(vaca: Vaca): VacaLoca = … // Devuelve una VacaLoca
def j(vacaLoca: VacaLoca): Vaca = … // Recibe una VacaLoca
def k(animal: Animal): Vaca = … // Recibe un Animal

f = ???

f(new Vaca).ordeñate
~~~

Cuales de las funciones definidas podrían guardarse en f ?

~~~scala
f = g  // Ok. Recibo una vaca y devuelvo una vaca. No problem.
f = h  // No! Si h devuelve un animal no puedo garantizar que entienda muji!
f = i  // Esto vale. La VacaLoca es una vaca y la puedo usar tal.
f = j  // No! j espera una VacaLoca, no puedo decir que espera sólo una vaca.
       // Si le paso una vaca a f y adentro le manda reite() se rompería!
f = k  // Si! k sólo pide que su parámetro sea un Animal y le habla como tal.
       // Entonces puedo pasarle una Vaca, que es un Animal.
~~~

En el caso de las funciones, el parámetro de tipo asociado al retorno varía en el mismo sentido que la jerarquía (o sea, admite casos más ESPECIFICOS del tipo que tiene declarado en el parámetro). A esto le decimos ser **Covariante**.

Por otro lado, los tipos de sus parámetros varían en el sentido opuesto (admite casos más GENÉRICOS). Son **Contravariante**.

En Scala eso se hace con una anotación en el tipo:

~~~scala
class Function1[-P,+R]{ // Clase de las funciones de un parámetro.
                        // El - adelante de P indica que es CONTRAVARIANTE.
                        // El + adelante de R indica que es COVARIANTE.
…
}
~~~

También se puede decir que un parámetro sea covariante o contravariante a partir de cierto punto de la jerarquía de tipos.

~~~scala
class Foo[+T <: Vaca, -R >: Animal] {
    // T es COVARIANTE para los subtipos de Vaca.
    // Puedo pasarle una Vaca o una Vaca loca, pero no un Animal.
    // R es CONTRAVARIANTE para los supertipos de Animal.
    // Puedo pasarle, por ejemplo, un Object.
…
}
~~~

### Ejemplo de contravarianza
Creemos una clase abstracta Printer que define un método print que imprime por la consola un objeto del tipo T: 
```scala
abstract class Printer[-T] {
  def print(t: T): Unit
}

// Sabe imprimir animales
class AnimalPrinter extends Printer[Animal] {
    override def print(t: Animal): Unit = {
      println(s"Este animal pesa: ${t.peso}")
    }
}

// Sabe imprimir vacas locas
class VacaLocaPrinter extends Printer[VacaLoca] {
    override def print(t: VacaLoca): Unit = {
      println(s"Una vaca loca se ríe así: ${t.reite}")
    }
}
```

Veamos ejemplos de uso

```scala
var printer: Printer[VacaLoca] = new VacaLocaPrinter
printer.print(new VacaLoca) // imprime: "Una vaca loca se ríe así: Muajajajjaajja"
```

Y como Printer es contravariante con respecto a su parámetro de tipo T, podemos guardar un objeto de tipo Printer[Animal] en una variable de tipo Printer[VacaLoca]
```scala
printer = new AnimalPrinter
printer.print(new VacaLoca) // imprime: "Este animal pesa: 100"
```
Esto es correcto porque print de AnimalPrinter espera un parámetro de tipo Animal (puede imprimir cualquier animal), y la variable printer solo acepta VacaLoca, que entiende todos los mensajes de Animal.

-------------------

## Ejercicio - Parte 2
Volvamos a pensar el problema que teníamos con las herramientas nuevas que aprendimos.

El problema del pastor era que él sabía trabajar con una colección de Animales, pero el corral tenía una colección de Vacas. Ahora entendemos el hay un problema con la varianza.

Sin embargo, para la mayoría de los casos pareciera que una colección de vacas podría ser tratada como una colección de animales… Sería copado que las colecciones fueran COVARIANTES, no? Así el pastor podría trabajar sin problemas con las vacas del corral.

Por supuesto, como señalamos antes, el problema de las colecciones pasa por los mensajes que trata de exponer. Si fuera covariante corremos el riesgo de que alguien agregue un objeto que rompa su contrato. Uhm… Y si trabajaramos con colecciones que no pueden romperse? Bueno, entonces no habría problemas. Pero cómo creo una colección que no se pueda romper? Lo que hay que hacer es renunciar a todos los mensajes problemáticos!

Resulta que si le quito los métodos que reciben por parámetro el tipo paramétrico, la colección podría definirse como covariante. (En realidad es más complejo que eso y depende de en DONDE se está referenciando al tipo paramétrico, pero bleh… Si quieren el detalle lean.)

La clase List es una colección que está implementada para ser **Covariante**. Eso significa que **List[Vaca] ES List[Animal]**.

List no tiene add(), hay que usarla como las listas de Haskell, construyendo otra. De hecho, no tiene ningún efecto de lado: Es **Inmutable**.

Entonces el código podría quedar así:

~~~scala
class Lechero {
  def ordeñar(corral: Corral[Vaca]) = corral.animales.foreach(_.ordeñate)
}
	
class Pastor {
  def pastorear(animales: List[Animal]) = animales.foreach(_.come)
}

class Corral[T <: Animal](val animales: List[T])

val corralito = new Corral(List(new Vaca, new Vaca, new Vaca))
val lechero = new Lechero
val pastor = new Pastor

pastor.pastorear(corralito.animales) // Ahora sí! Esto anda!
lechero.ordeñar(corralito)           // Esto también! Yupi!
~~~

Ojo! Que la lista sea inmutable no significa que el corral tenga que serlo. De hecho, alcanzaría con cambiar el val por un var.

Elegir en donde tener efecto colateral y en donde no es una decisión de diseño REEEE importante.

### Covarianza
Aprovechando las nuevas herramientas, podemos definir que los corrales de vacas sean subclases de los corrales de animales, usando covarianza:
```scala
class Corral[+T <: Animal](val animales: List[T])

// Ahora podemos hacer esto:
val corralDeVacas: Corral[Vaca] = new Corral(new Vaca, new Vaca, new VacaLoca)
var corralDeAnimales: Corral[Animal] = corralDeVacas // Ahora es válido
```

La covarianza no viene gratis, una de las grandes limitantes es que no podemos definir un método que reciba T, porque T es covariante y el parámetro de una función es una posición contravariante:
```scala
class Corral[+T <: Animal](val animales: List[T]) {
  def contiene(t: T): Boolean = ??? // covariant type T occurs in contravariant position in type T of value t
}
```

Lo que podemos hacer para mitigar esto es usar lower bounds:

```scala
class Corral[+T <: Animal](val animales: List[T]) {
  // [T1 >: T] significa "T1 tiene que ser una superclase de T"
  def contiene[T1 >: T](t: T1): Boolean = ??? // covariant type T occurs in contravariant position in type T of value t
}
```

Esto nos permite evitar en tiempo de compilación que se pueda llamar con cualquier objeto (que es imposible que sea contenido por el corral):

```scala
corralDeVacas.contiene[Int](123) // type arguments [Int] do not conform to class Corral's type parameter bounds [+T <: granja.Animal]

// La desventaja es que también se puede
corralDeVacas.contiene[AnyRef]("Vaca") // Funciona porque AnyRef es una superclase de Vaca
```