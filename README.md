# Objeto-Funcional en otros Lenguajes

##[[TODO]]
 "porqué estos lenguajes (breve descripción)"
    - Kotlin tomó mucho de Scala. Intermedio entre Java y Scala.
    - ES y qué es.
    - TS es ES tipado con cuidado de no cruzar la linea.
 "links importantes a los lenguajes"
 "La idea es plantear cómo se están encarando los issues que presentamos en otras técnologías y ver de paso algunas herramientas y nociones nuevas que giran alrededor de problemas similares"

## Tipado

Vamos a empezar planteando algunas variantes interesantes a los sistemas de tipos de los lenguajes que usamos durante la cursada. El tipado de *Scala* es de los más seguros, flexibles y, por lo tanto, complejos de los lenguajes orientados a objetos. *Kotlin* toma muchas de sus ideas y define un tipado algo más rígido y menos preciso pero mucho más simple, al mismo tiempo que agrega bastante boilerplate a su sintáxis para protejerse de (lo que algunos consideran) problemas comunes. Por otro lado, *TypeScript* abiertamente acepta su tipado como **unsound** y no ofrece una solución para las situaciones más complejas que otros lenguajes tratan de resolver pagando el costo de una mayor complejidad. Del sitio de *TypeScript*:

> TypeScript’s type system allows certain operations that can’t be known at compile-time to be safe. [...] The places where TypeScript allows unsound behavior were carefully considered, and throughout this document we’ll explain where these happen and the motivating scenarios behind them.

Basicamente el lenguaje aspira a que su tipado sea una mejor alternativa que el no-tipado de *EcmaScript* y está más preocupado por ser accesible que seguro. Ironicamente, esta laxedad en los chequeos permite luego tipar algunas construcciones complejas que en otros lenguajes más estrictos no serían posibles y tendrían que hacerse usando reflection u otros mecanismos inseguros.

Uno puede estar a favor o en contra de las decisiones particulares de estas tecnologías, pero hay una idea interesante escondida detrás que merece consideración: Los lenguajes (al igual que los problemas que buscan resolver) están atados a un tiempo, un público y un contexto. A veces puede ser buena idea alejarse del paradigma o implementar un concepto de forma menos (o más) rigurosa en pos de mejorar el uso cotidiano.

Vamos a mencionar entonces algunos de los aspectos más interesantes (para bien o para mal) de estos sistemas de tipos.

### Typescript: Tipandolo con pinzas

En sí, la filosofía de *TypeScript* consiste en ser una versión más segura de *EcmaScript*, manteniendose fiel a sus principios y sin introducir "features" que no puedan mapearse directamente al lenguaje original.
Esto implica no requerir un cambio muy abrupto en la forma de programar y preservar la naturaleza "flexible" de ES, lo cual no es fácil...

Para esto, *TypeScript* basa sus chequeos en un **Tipado Estructural**:

```typescript
let x: { a: number, b: string }

// Esto funciona
x = { a: 5, b: "foo" }

// Pero esto no...
x = { a: 7 }
```

Un detalle interesante es que, una vez que el lenguaje acepta que su tipado no es siempre seguro, se puede permitir hacer algunos chequeos que enfoques más estrictos descartarían por no ser consistentes:

```typescript
let x: { a: number, b: string }

// Esto, como es de esperarse, tipa.
let y = { a: 7, b: "bar", c: true }
x = y

// Pero esto no!
// TS asume que si harcodeas un literal acá, poner el "c" es probablemente un error.
x = { a: 7, b: "bar", c: true }

// Incluso se puede optar por incluir o excluir ciertas validaciones.
x = null
```

Otra noción *"impura"*, del lenguajes es su aproximación al **Tipado Nominal**. Es posible, además de tipos estructurales definir *Interfaces* de forma muy similar a otros lenguajes, con la diferencia de que las estructuras no necesitan declarar que las implementan de forma explícita. Esto quiere decir que uno no puede confiar en que el objeto fué pensado para cubrir un rol (y no sólo expone la interfaz por casualidad).

```typescript
interface T { a: number }

let t: T

// Esto, obviamente, tipa
class C implements T {
    a: number
}
t = new C()

// Pero esto también!
t = { a: 5 }
```

La consecuencia directa es que el aspecto nominal del tipado aporta más expresividad que otra cosa porque, en definitiva, los tipos son escencialmente estructurales. Esto no es ni bueno ni malo, aunque si tiene algunas limitaciones... Cuando alguien declara que quiere pertenecer al tipo `T` el compilador puede validar que implemente la estructura necesaria, pero cuando alguien espera recibir un parámetro de tipo `T` no hay manera de validar que no sea otra cosa con estructura similar. Por otro lado, esto permite que un objeto capaz de cumplir con un tipo no sea rechazado sólo porque no lo declara explicitamente.

¿Entonces, cuál es la moraleja? ¿Nos gusta o no nos gusta este "tipado laxo"? Y... Es distinto. Obviamente tenés menos garantías de que un programa que tipa funcione pero, una vez aceptado esto el lenguaje se puede permitir crecer más rápido o implementar conceptos más atrevidos.

Un ejemplo de esto es la **Conjunción y Disjunción de Tipos** que en *Scala* [llevan varios años discutiendo](https://contributors.scala-lang.org/t/whats-the-status-of-union-intersection-types-singleton-types-in-dotty) y *TypeScript* implementa sin ningún tipo de reparo.


```typescript
interface Alumno {
  nombre: string
}

interface Docente {
  legajo: string
}

let persona: Alumno | Docente  // Cualquiera de los dos!
let ayudante: Alumno & Docente // Los dos al mismo tiempo!
```

Incluso nociones más controversiales, como mezclar valores en la definición de un tipo, no mueve mucho la vara:

```typescript
interface Alumno {
    nombre: string
    nota: 2 | 6 | 8 | 9 | 10
    condicion: "ingresante" | "regular" | "irregular"
}
```

### Tipos Paramétricos y Varianza

Los típos parámetricos (o, como algunos lenguajes los llaman, **Generics**) consisten básicamente en permitir parametrizar la construcción de un tipo, agregando información que puede ser usada por el chequeador para resolver situaciones complejas, donde la interfaz de un objeto depende de factores externos. Si bien la idea general es bastante sencilla, no todos los lenguajes utilizan estas herramientas del mismo modo. En clase cubrimos (casi todo) el uso que el sistema de tipos de *Scala* hace de estos parámetros y la manera en que decide cómo se relacionan los tipos en función a como se relacionan sus parámetros (**Varianza**), pero sería un error pensar que todos los lenguajes llegan así de lejos para mantener la consistencia de sus tipos. Sin ir más lejos *Java*, el punto de referencia muchos lenguajes modernos, no maneja varianza de tipos sino que se conforma con cubrir a medias esas situaciones usando [un mecanismo de wildcards](http://www.angelikalanger.com/GenericsFAQ/FAQSections/TypeArguments.html#Topic2).

*Kotlin*, pese a haber tomado gran parte de sus abstracciones de *Scala* y apuntarlas a usarios de *Java*, decidió que no le gustaba ni un enfoque ni el otro. Del sitio de *Kotlin*:

> One of the most tricky parts of Java's type system is wildcard types (see Java Generics FAQ). And Kotlin doesn't have any.

Me gusta el detalle de que llama a los wildcards *"tricky"*, y no *"difíciles"*, porque abre la puerta a una discusión interesante. **Varianza**, como tantos otros conceptos con un contenido teórico fuerte, es un tema bastante complejo. Para usarlo bien, es necesario aprender los fundamentos y leer a los autores que estudiaron el tema. En ocasiones los lenguajes industriales optan por no seguir el camino que marca la academia (a veces porque creen tener una propuesta mejor, a veces porque no les interesa tanto un tema y prefieren ahorrar complejidad e invertirla en otra cosa y a veces porque abiertamente desconocen la teoría). En estos casos, los mismos problemas pueden resolverse a los ponchazos, con construcciones simplificadas y especializadas para un uso particular, lo cual, a la larga, puede terminar en una pila de herramientas heterogéneas que se solapan o no terminan de cubrir todos los casos de uso. Lo gracioso es que estas herramientas simplificadas muchas veces terminan teniendo tantos casos especiales que resulta más complicado aprenderlos todos que leer la teoría que tratan de evitar.

En fin... *Kotlin*, que no quería los parches de *Java* ni quiso pagar la complejidad de *Scala* apostó por un mecanismo de tipado similar pero más sencillo, que [tomó prestado de .NET](https://docs.microsoft.com/en-us/dotnet/standard/generics/covariance-and-contravariance#DefiningVariantTypeParameters).

Este enfoque permite definir *Covarianza* y *Contravarianza* similar al `-T` y `+T` de *Scala*, pero usando las palabras clave `in` y `out`, respectivamente.

```kotlin
class Caja<out T> { }

fun main(args: Array<String>) {
  var a: Caja<Any> = Caja<String>() // Esto funciona
  var b: Caja<String> = Caja<Any>() // Esto no
}
```

Como nota sobre la expresividad, las palabras clave `in` y `out` son más claras respecto a las restricciones que imponen sobre dónde es posible usar cada tipo (in => parámetros, out => retorno), mientras que los símbolos `+` y `-` resultan cómodos a la hora de pensar cómo se combinan las varianzas ("menos por menos es más" se puede mapear fácilmente a "contravariante de contravariante es covariante"). Esto da lugar a pensar qué va a tener el desarrollador en la cabeza al momento de escribir el código y dónde conviene ayudarlo...

```kotlin
class X<in T> {}

fun main(args: Array<String>) {
    var fx : (X<Any>)    -> Unit = {_ -> }
    var gx : (X<String>) -> Unit = {_ -> }

    // Qué tiene que ver ser "in" con todo de esto???
    gx = fx // Falla!
    fx = gx // Funciona: Contravariante de Contravariante.
}
```

*Kotlin* complementa su sistema de generics con una sintaxis para definir **Upper Bounds** (Pero no **Lower Bounds**):

```kotlin
class Corral<T : Animal> {} // T debe ser subtipo de Animal
```

También, a diferencia de *Scala*, *Kotlin* permite restringir la interfaz de un tipo con tipo paramétrico invariante para forzarlo a restringir su interfaz como si fuera covariante/contravariante. A esto lo denomina **Proyección de Tipo**:

```kotlin
// Si queremos tener getter y setter de contenido, T debe ser invariante.
class Caja<T>(var contenido: T) {
    fun copiar1(otro: Caja<T>){ this.contenido = otro.contenido }
    fun copiar2(otro: Caja<out T>){ this.contenido = otro.contenido }
}

fun main(args: Array<String>) {
    val a: Caja<Any> = Caja(7)
    val b: Caja<Int> = Caja(5)
    
    a.copiar1(b) // Falla porque a y b tienen distinto tipo
    a.copiar2(b) // Pero si forzamos el parámetro covariante funciona!
}
```

En el otro extremo del espectro, [si bien puede configurarse para hacer algunos controles básicos](https://www.typescriptlang.org/docs/handbook/release-notes/typescript-2-6.html), *TypeScript* decide evitarse el problema y hacer todos los generics **Bivariantes**:

```typescript
class Animal { }
class Vaca extends Animal { }
class VacaLoca extends Vaca { }

class Corral<T extends Animal> { f(t: T) { return t } }

let a: Corral<Animal> = new Corral()
let b: Corral<Vaca> = new Corral()
let c: Corral<VacaLoca> = new Corral()

b = a // Sep.
b = c // No veo porqué no...
```

De más está decir que esto no es lo más seguro, pero *TypeScript* elige poner la responsabilidad de evitar esos problemas en el usuario a cambio de permitirle permanecer ignorante sobre teoría de varianza y mantener el tipado suficientemente sencillo para implementar...


### Features locos

El razonamiento es simple: Desde el punto de vista del usuario, el sistema de tipos es confiable o no (no importa *porqué*). Si ya sé que tengo que estar atento cuando uso ciertas construcciones y lo acepto como parte del uso cotidiano del lenguaje, entonces es posible agregar herramientas interesantes aunque no pueda hacerlas tipar de forma completamente consistente. Vamos a mencionar un par de ejemplos de esto presentes en *TypeScript*.

#### Index Types

With index types, you can get the compiler to check code that uses dynamic property names.

*TypeScript* permite el mismo uso de propiedades dinámicas que *EcmaScript*. Esto incluye referenciar el nombre de propiedades con construcciones no-estáticas (Ej.: obj["propiedad"] en lugar de obj.propiedad). Los [Index Types](https://www.typescriptlang.org/docs/handbook/advanced-types.html#index-types) son la construcción sintáctica que permite que el compilador analice código que usa nombres dinámicos de propiedades.

```typescript
class Alumno {
    nombre: string
    legajo: number
}

let campo: keyof Alumno // Esto tiene tipo "nombre" | "legajo"
let tipoDeCampoNombre: Alumno["nombre"] // Tiene tipo "string"
let tipoDeCualquierCampo: Alumno[keyof Alumno] // Tiene tipo "string" | "number"
```

Parece **Reflection**, no? Pero hay que notar que no usamos ninguna expresión de runtime; esa información está en los tipos y, por lo tanto, es accesible en tiempo de compilación.

Este tipo de sintáxis desdibujan la linea que separa el código cotidiano de la *metaprogramación* y permite hacer esta última de forma (un poco) más segura. Tomemos por ejemplo esta función:

```typescript
function dameElCampo<T, K extends keyof T>(obj: T, key:K): T[K] {
  return obj[key]
}

let pirulo: Alumno

// Funciona y se da cuenta de que tiene tipo "string"
dameElCampo(pirulo, "nombre")

// Falla! El alumno no tiene el campo pedido.
dameElCampo(pirulo, "qué pirulo?")
```

El código de este ejemplo es básicamente un wrapper del acceso con corchetes que recibe dos tipos paramétricos: uno es el tipo del objeto del cual queremos el campo y el otro es **un subtipo de las claves presentes en dicho objeto**. Al momento de aplicar la función, el compilador va a inferir dicho tipo en base al parámetro que pasamos, permitiendole identificar también el tipo más especifico para retornar (en lugar de retornar `"string" | "number"`) y permite detectar en compilación un error tradicionalmente de runtime.

#### Mapped Types

Otra construcción interesante son los [Mapped Types](https://www.typescriptlang.org/docs/handbook/advanced-types.html#mapped-types) que, básicamente, permiten definir un tipo a partir de los campos de otro:

```typescript
type Opcional<T> = {
  [K in keyof T]: T[K] | null
}

let tipoDeNombre: Opcional<Alumno>["nombre"] // Tipa a "string" | "null"

// Falla! legajo es de tipo number y no puede ser null
let jose: Alumno = { nombre: "jose", legajo: null }

// Ahora sí...
let joseOpcional: Opcional<Alumno> = { nombre: "jose", legajo: null }
```

En este ejemplo se define el tipo `Opcional<T>`, que expone todos los campos de `T`, pero cambiando su tipo por la unión entre este y null.

El uso de *mapped types* es sorpresivamente recurrente, al punto en que ya vienen varios predefinidos (Ej: Readonly, Partial, Pick, Record, etc.) e incluso la misma comunidad suele [proponer cosas locas](https://github.com/Microsoft/TypeScript/issues/13257).

Dando un paso atrás, es bastante evidente porqué estas construcciones son más raras en lenguajes con un tipado más estricto. De hecho, en *Scala* sólo sería posible modelar una abstracción como *mapped types* usando [whitebox macros](https://docs.scala-lang.org/overviews/macros/blackbox-whitebox.html) (macros cuyos tipo no puede establecerse en tiempo de compilación y que muchos proponen discontinuar justamente porque al tipador no le gustan) y aun así es difícil...

Combinando estas herramientas con los otros operadores de tipos es posible construir operadores muy avanzados:

```typescript
// Miembros comunes a T y U.
type Diff<T extends string, U extends string> = ({ [P in T]: P } & { [P in U]: never } & { [x: string]: never })[T]

// Miembros de T que no están presentes en U.
type Omit<T, K extends keyof T> = { [P in Diff<keyof T, K>]: T[P] }

// Miembros de T y U donde las definiciones de U prevalecen.
type Overwrite<T, U> = { [P in Diff<keyof T, keyof U>]: T[P] } & U
```
[Acá](https://www.stevefenton.co.uk/2017/11/typescript-mighty-morphing-mapped-types/) hay un artículo lindo que explica un poco esto.

### Antes y Después del Compilador

Tanto *Kotlin* como *TypeScript* permiten algún grado de **Inferencia de Tipos**. En general, la gran mayoría de los lenguajes con tipado explicito (incluso [C++](https://dgvergel.blogspot.com.ar/2016/04/inferencia-automatica-de-tipos-auto.html)!) tratan de incorporar esto a sus sintáxis, para reducir el boilerplate y hacer más fácil la transición desde tecnologías más dinámicas. Es probable que lo único que previene que esto se convierta en el standard de la industria es que no terminamos de ponernos de acuerdo en si el tipo escrito mejora o empeora la lectura del código.

Sobre esto *Kotlin* es bastante opinionado: fuerza al usuario a explicitar algunos tipos que infiere, porque [considera que el código resultante es más claro](https://discuss.kotlinlang.org/t/type-inference-for-return-types-of-longer-functions/554/2).

```kotlin
class X {
  fun m() = "foo" // Obviamente retorna un String.
  fun n() {
    return "bar"  // Hey! Hey! Despacio cerebrito!
  }
}
```

De acuerdo o no, es interesante pensar que estás restricciones que parecen de menor importancia en la sintáxis de un lenguaje pueden empujar a la comunidad a adoptar ciertas prácticas (en este caso, usar más métodos definidos como expresiones y menos bloques largos).

Otro patrón recurrente, un poco menos feliz, es que ambos lenguajes también decidieron descartar la información de tipos en runtime. *Kotlin*, que originalmente se compilaba para la *JVM*, pasa por el mismo proceso de **Erasure** que *Scala*, donde los tipos paramétricos se descartan post compilación.

*TypeScript* va un paso más lejos y **elimina toda información sobre los tipos**, compuestos o no, para compilar al código *ES* más similar posible. Esto hace imposible usar esta información para hacer *introspection* en runtime...

Si bien existen lenguajes donde los tipos compuestos están reificados a nivel plataforma ([como .NET](https://en.wikipedia.org/wiki/Generic_programming#Genericity_in_.NET_[C#,_VB.NET])) y otros ([como *Scala*](https://docs.scala-lang.org/overviews/reflection/typetags-manifests.html)) que encontraron alguna forma de dibujarla, en general la postura suele ser que eliminar esta información es lo más rápido y fácil de hacer y evita que los programas "engorden" guardando metadata que no siempre necesitan.

De ahí que existan cosas como el [Projecto Valhalla](https://en.wikipedia.org/wiki/Project_Valhalla_(Java_language)), que aspira a agregar soporte para este y otros features a la *JVM*, lo cual podría cambiar radicalmente el modo como otros lenguajes manejan los generics.


## Definición de Objetos

Mencionamos en clase varias veces que el paradigma de objetos todavía no se decide sobre cuál es la mejor manera de definir, instanciar y asociar comportamiento a los objetos. Esto lleva a que la gran mayoría de los lenguajes prueben sus propias variantes de herramientas y metamodelos; con lo cual, si bien existen algunos patrones comunes, ningún enfoque superador se impuso todavía.

### Kotlin: Entre la Scala y la pared

En muchos sentidos, *Kotlin* toma sobre estos temas un enfoque muy conservador, adoptando la **Herencia Simple con Default Methods (interfaces con código)** de *[Java 8](https://docs.oracle.com/javase/tutorial/java/IandI/defaultmethods.html)* como su mecanismo principal de subtipado.

```kotlin
interface Humano {
    fun cantar() = "Lalalala"
}

interface Caballo {
    fun relinchar() = "Ihihihihi"
}

class Centauro : Humano, Caballo { }
```

Para el ojo poco entrenado de un tipo optimista, estas interfaces pueden parecer **Mixins** al estilo de *Scala*, pero cualquier alumno de TAdP sabe que hay que hacerse algunas preguntas antes de adelantar conclusiones:

- **¿Pueden definir estado?**
  No. Las properties de las interfaces son abstractas.
  ```kotlin
  interface I {
    var x: String
  }

  // Falla: Falta implementar x!
  class C: I { }
  ```

- **¿Los conflictos se resuelven automáticamente (por ejemplo, linearizando)?**
  Nop. Es necesario sobreescribir a mano cada método conflictivo. Las implementaciones heredadas están disponibles con una sintáxis especial.
  ```kotlin
  interface I {
    fun m() = "foo"
  }
  interface J {
    fun m() = "bar"
  }

  interface IJ: I, J {
    override fun m(): String {
        return super<J>.m()
    }
  }
  ```

- **¿El super es dinámico?**
  A efectos prácticos, no. Justamente porque las interfaces no se linearizan no puedo hacer un llamado a `super` que no refiera a un método concreto.
  ```kotlin
  interface I {
    fun m(): String
  }

  interface J: I {
    // Falla: m es abstracto.
    override fun m() = super<I>.m()
  }
  ```

Vemos entonces que estas abstacciones están mucho más cerca de los *Traits* de *Ducasse* que a los *Mixins* de *Bracha*.

Es cierto que esta no es la opción más flexible o limpia pero, en defensa(?) de *Kotlin*, no es descabellado que un lenguaje que aspira a ser el sucesor de *Java* decida no introducir cambios abruptos en las herramientas de diseño Orientado a Objetos en pos de pelear otras batallas. Dicho esto, sí, duele un poco considerar que el mismo *Java* intrudujo estas abstracciones tarde y medio como una solución de compromiso, porque introducir algo más disruptivo comprometería la **retrocompatibilidad**, con lo cual *Kotlin* se ve limitado por decisiones que se tomaron para la primer JVM, hace más de 20 años. ¿Triste? Puede, ser... Pero este tipo de condicionamiento [no es nada nuevo](https://clipset.20minutos.es/como-el-culo-de-un-caballo-condiciona-la-tecnologia-espacial-de-la-nasa/).

En un tono más positivo, *Kotlin* también toma de *Scala* varias de sus ideas más originales sobre cómo obtener objetos.

Un ejemplo de esto es que se pueden definir **Singleton Objects**,tanto anónimos como globales, para cuando sólo se necesita una única instancia.

```Kotlin
object EstoEsUnObjeto {
    val estoTambién = object {}
}
```

Otra gran decisión fue reemplazar la idea de **constructor** por la de **parametros de clase** (a los que llaman *"constructores"* pero shh...).

```Kotlin
// Los parámetros por defecto cubren la mayor parte de los casos de sobrecarga.
class X(val a: Int, var b: String = "") {
    var c: String
    
    // Cualquier efecto que no sea inicializar un campo va en el bloque init (en lugar de estar desperdigado en el cuerpo de la clase).
    init {
         c = if (b == "foo") "bar" else "baz"
    }
    
    // En caso de necesitar otra firma, puedo hacer esto (o usar un object como en Scala).
    constructor(d: Boolean) : this(0, "")
}

fun main(args: Array<String>) {
    // Noten que no existe el "new"!
    val xa = X(5).a
}
```

También incorpora el concepto de **Companion Object** para lidiar con el aspecto estático de las abstracciones, aunque va un paso más lejos y hace que tenga que declararse dentro de la clase misma: 

```kotlin
class X {
  companion object {
      fun m() = 5
  }
}

fun main(args: Array<String>) {
  val n = X.m()
}
```

Por último, *Kotlin* también reñiega de la ambiguedad entre **atributos** y **accessors**, descartandolos en favor de **properties**, sin embargo, su aproximación a las mismas es mucho más similar a la de *C#* que a la de *Scala*.

```kotlin
class X {
  // Puedo sobreescribir los accessors refiriendome al atributo con una palabra clave.
  var p: Int = 0
    get() {
        println("Me están leyendo el campo")
        return field
    }
    set(value: Int) {
        println("Me cambiaron el campo de ${field} a ${value}")
        field = value
    }

  // También puedo sólo inventar properties calculadas
  val f
    get() = 7
}

fun main(args: Array<String>) {
    val x = X()
    x.p += x.f
}
```

### Typescript/ES: Ahora con... Clases?

Vayamos ahora al otro extremo del espectro: ¿Qué ideas locas e inovadoras sobre cómo modelar objetos se introdujeron ultimamente en los lenguajes más dinámicos?

Clases.
![what year is it???](http://i.lvme.me/156wu9.jpg)

Eso. Desde su versión *6*, *EcmaScript* incorpora una reificación del concepto de **Clases con Herencia Simple** y, obviamente, *TypeScript* traslada esto a su propio modelo.

Que giles, no? Y... No. En realidad, la cosa es un poco más compleja...

La incorporación de Clases en *ES* es un cambio casi puramente cosmético. La sintáxis nueva incorpora una serie de azucares sintácticos para reducir el boilerplate, pero lo cierto es que el metamodelo de ES basado en **Prototipos** soporta perfectamente "simular" un árbol de clases y, de hecho, [lo vienen haciendo desde hace tiempo](https://www.webreflection.co.uk/blog/2015/11/07/the-history-of-simulated-classes-in-javascript).

La extensión basicamente implica poder escribir el código así:

```typescript
class Persona {
    nombre: string

    constructor(nombre) {
        this.nombre = nombre
    }

    id() { return this.nombre }
}

class Alumno extends Persona {
    legajo: number

    constructor(nombre, legajo) {
        super(nombre)
        this.legajo = legajo
    }

    id() { return this.legajo + "-" + super.id() }
}

let pirulo = new Alumno("pirulo", 148)
```

en lugar de así:

```typescript
function Persona(nombre) {
    this.nombre = nombre
}
Persona.prototype.id = function() { return this.nombre }

function Alumno(nombre, legajo) {
    Persona.call(this, nombre)
    this.legajo = legajo
}
Alumno.prototype.prototype = Persona
Alumno.prototype.id = function() {
  return this.legajo + "-" + this.prototype.prototype.id.call(this)
}

let pirulo = new Alumno("pirulo", 148)
```

Una observación interesante sobre este último código es que *ES* trabaja con el concepto de **Constructor Functions** (tal vez un ancestro conceptual de los **Parámetros de Clase**) donde el `new` simplemente crea un nuevo objeto para cumplir el rol the `this` en una función cualquiera, que lo configura a criterio.

De forma similar podemos también modelar **Mixines** como se explica en [este excelente artículo](http://justinfagnani.com/2015/12/21/real-mixins-with-javascript-classes/):

```typescript
// Los mixines pueden modelarse como funciones!
let M1 = (next) => class extends next {
  m() { return super.m() * 2 }
}

let M2 = (next) => class extends next {  
    m() { return super.m() + 10 }
}

class S {
    m() { return 5 }
}

// Al definir la clase, linearizamos de afuera hacia adentro.
class C extends M1(M2(S)) {
    m() { return super.m() + 1 }
}

new C().m() // 31

// Incluso podemos instanciar los mixines!
new (M1(M2(S)))().m() // 30
```

No se dejen engañar por la falta de tipos y multithreading, *ES* tiene desde hace años uno de los metamodelos más simples, poderosos y flexibles de la industria. Sí, su sintáxis tiene varias limitaciones y algunas decisiones del modelo base dejan bastante que desear, pero la mayor parte de estos inconvenientes son superficiales y facilmente vadeables, al punto que hoy en día existen [cientos de lenguajes](https://github.com/jashkenas/coffeescript/wiki/list-of-languages-that-compile-to-js) que trasladan su sintáxis a lo que *ES* tiene abajo del capot (incluyendo a *Kotlin*).


## Inmutabilidad y Efecto

Trabajar sin efecto es una de las facetas de la programación funcional más abrazada por los lenguajes nuevos. Lamentablemente, muchas tecnologías se concentran en detalles que solamente rascan la superficie de la idea, o descartan la posibilidad de producir efectos por completo (y con ella, una parte escencial de la programación Orientada a Objetos). Vamos a marcar entonces algunas de las abstracciones más interesantes que los lenguajes modernos encontraron en su busqueda de un mejor control del efecto.

### Constantes

Casi todos los lenguajes modernos (y muchos de los clásicos) tienen abstracciones que identifican a una referencia como **constante** (o **final**). Básicamente esto significa que la referencia en cuestión puede ser asignada sólo una vez, durante su inicialización, pero no puede ser reasignada luego.

El empuje que el enfoque funcional tuvo en los últimos años propició un cambio en el rol que estas referencias tienen en el código, pasando de ser usadas por muchos casi exclusivamente para evitar repetir el hardcodeo de valores bien conocidos a ser la manera estandard de separar resultados parciales en el código. Además, el soporte para properties inmutables a nivel sintáxis facilitó la popularización del trabajo sin efecto en la POO y abrió las puertas a nuevas preguntas.

Uno de estos planteos pasa por integrar la inmutabilidad al **ciclo de vida de los objetos**. Muchos lenguajes donde la sintáxis fuerza la separación entre la inicialización de un objeto y la definición de sus variables debieron adecuar su definición de *constante* para permitir que sean inicializadas en su definición o **durante la inicialización** del objeto.

**Kotlin:**
```kotlin
class Persona(edad: Int) {
    val esAdulto: Boolean
    
    init {
      require(edad in 0..100){ "mala edad" }        
      esAdulto = edad > 18
    }
}
```


**Typescript:**
```typescript
class Persona {
    readonly edad: number
    readonly esAdulto: boolean

    constructor(edad: number) {
        if (edad < 0 || edad > 100) throw new Error("mala edad")

        this.edad = edad

        // Estos chequeos de compilación no son triviales...
        this.edad = edad

        // Typescript usa const para las variables y el modificador de tipo readonly para las properties
        const esAdulto = edad > 18
        this.esAdulto = esAdulto
    }
}
```

Lo interesante es que, una vez abierta la puerta a la discusión de que tal vez hay más de una forma de variable, rapidamente aparecen propuestas nuevas. *Kotlin*, que fue originalmente pensado como un lenguaje para *Android* donde el espacio de almacenamiento tiene una importancia especial, define una sintáxis para un tipo de constantes estáticas que son embebidas en el lugar donde se referencian en tiempo de compilación (permitiendo potencialmente que la clase donde están definidas sea removida por el optimizador).

```kotlin
// Los const son embebidos en compilación y sólo pueden ser de tipos primitivos o strings.
const val FOO = "BAR"

// Estas dos funciones se compilan a lo mismo
fun f() = FOO
fun g() = "BAR"
```

También incorpora una sintáxis que permite postergar la inicialización de variables sin comprometer su tipo.

```kotlin
class Docente

class Curso {
    // Las referencias (vars o vals) deben ser inicializadas siempre...
    var docente: Docente
    
    // ...salvo los var marcados como lateinit, que pueden inicializarse después.
    lateinit var jtp: Docente
}

fun main(args: Array<String>){
    // Si un lateinit no se inicializa antes de leerse, rompe en runtime.
    println(Curso().jtp)
}
```

*TypeScript* también viene con abstracciones interesantes para las constantes, de la mano de los **Mapped Types**.

```typescript
type Readonly<T> = {
    readonly [P in keyof T]: T[P];
}

type DeepReadonly<T> = {
    readonly [P in keyof T]: DeepReadonly<T[P]>;
}

class Docente {
    nombre: string
}

class Curso {
    docente: Docente
    jtp: Docente

    tieneDocente() { return this.docente != null }
}

// Como readonly es sólo un modificador de tipo no necesito cambiar código
// puedo usar un curso cualquiera y sólo lo "veo" distinto. 
let curso: Curso
let cursoRO: Readonly<Curso> = curso
let cursoDRO: DeepReadonly<Curso> = curso


curso.docente = new Docente()    // Que mal! Si retorno mi curso pueden cambiarmelo.
cursoRO.docente = new Docente()  // Que bien! Puedo hacer sus campos readonly!
cursoRO.docente.nombre = "Toto"  // Que mal! Sigue siendo mutable...
cursoDRO.docente.nombre = "Toto" // Que bien! Cascadeo el readonly!
cursoDRO.tieneDocente() // Pero el DeepReadonly no tiene la operación para evaluarse... Que mal!
```

Todo esto está muy bien pero, como se trató en clase, trabajar sin efecto requiere más que solamente tener variables que no pueden ser reasignadas, es necesario también contar con buenas herramientas para transformar estructuras, definir interfaces que no requieran de mantener un estado y provean alternativas limpias al lanzado de excepciones como mecanismo de control de flujo.


### Expresiones Vs. Sentencias

Podemos pensar en las **Expresiones** como **Sentencias** que retornan un **Valor**, en contraposición a aquellas que sólo producen un efecto. Los lenguajes con fundamentos funcionales (como *Kotlin*) hacen hincapié en que todas (o al menos la mayoría) de sus sentencias son expresiones. Esto permite escribir cualquier sentencia donde se espera un valor, favoreciendo un estilo de escritura menos procedural.

*TypeScript* no tiene tanta suerte, ya que varias de sus construcciones y **Clausulas de Control de Flujo** no son expresiones:

```typescript
let condicion: boolean

// El if no retorna un valor, así que no puedo asignarlo.
const n1 = if (condicion) 1; else 2;

// Esto nos fuerza a separar la definición de la inicialización
// y nos impide usar const...
let n2
if (condicion)
    n2 = 1
else
    n2 = 2

// Existe un operador ternario (sólo para el if) que SÍ es una expresión.
const n3 = condicion ? 1 : 2

// Sin embargo, sólo admite expresiones como parámetro.
// El throw no es una expresión, así que puede usarse acá.
const n4 = condicion ? 1 : throw "ufa"
// Ni tampoco usar más de una sentencia.
const n5 = condicion ? 1 : {
    console.log("la condición fue falsa")
    2
}
```

Un truco recurrente para lidiar con esto es envolver las sentencias con **Lambdas** o **Funciones** (aunque la expresividad sufe un poco...):
```typescript
let condicion: boolean

// Noten que definimos si y no como funciones, para mantener la evaluación diferida.
function ifThenElse(cond, si, no) {
    if (cond) return si()
    else return no()
}

const n1 = ifThenElse(condicion, () => 1, () => 2)

const n2 = condicion ? 1 : (() => { throw "ufa" })()
```


### Transformación de datos inmutables

Al trabajar sin efecto es habitual simular un cambio sobre una estructura creando una copia de la misma que cuente con la diferencia deseada. Lamentablemente, esto suele ser más fácil de decir que de hacer, ya que generar copias con cambios complejos y anidados tiende a ser una tarea incomoda y verbosa.
Como vimos en la cursada, *Scala* ofrece algunas facilidades superficiales para esto en sus **Case Classes**, las cuales cuentan con mecanismos para destructurarse y copiarse. Estos mecanismos, si bien útiles para cosas sencillas, carecen de una integración más profunda al metamodelo y no pueden en general ser heredados, extendidos o utilizados polimorficamente; lo cual hace que desde hace tiempo [la comunidad los quiera mejorar](http://www.scala-lang.org/old/node/5364).

*Kotlin* (un poco desperdiciando la oportunidad de plantear algo más interesante) tomó la idea de Case Class (renombrándola a **Data Class**) casi al pie de la letra, con la diferencia de que no basan sus mecanismos de deconstrucción en [un contrato de mensajes como el de *Scala*](https://docs.scala-lang.org/tour/extractor-objects.html), haciendolo un poco menos poderoso.

```kotlin
data class Alumno(val nombre: String, val nota: Int)

fun main(args: Array<String>) {
    val pepe = Alumno("Pepe", 7)
    val (_, notaDePepe) = pepe
    val pipo = pepe.copy(nombre = "pipo", nota = notaDePepe + 1)
}
```

En contraste, no es raro que *ES* (y, por transición, *TypeScript*), que desde sus origines es utilizado de forma exhaustiva para consumir y manipular datos tontos, haya invertido mucho tiempo y esfuerzo a mejorar la sintaxis y herramientas con las que transforma estructuras.

```typescript
interface Alumno {nombre: string, nota: number}
interface Materia { nombre: string }
interface Curso { materia: Materia, alumnos: Alumno[] }

const unAlumno = { nombre: "pepe", nota: 7 }
// Podemos definir variables siguiendo un patrón basado en la estructura.
// A esto le llamamos destructurar un objeto.
// En caso de que el campo no exista podemos darle un default.
const {nombre, nota = 0} = unAlumno

// Podemos extraer sólo los campos que nos interesan.
const aprobo = ({ nota }: Alumno) => nota > 6

// Podemos deconstruir multiples niveles (e incluso inferir el tipo estructural).
const necesitaUnApodo = ({ nombre: { length } }) => length > 10
    
// Podemos copiar un objeto "untandolo" en otra definición.
// Noten que el campo alumnos es pisado... Este es nuestro copy.
const desdoblar = (curso: Curso) => ({...curso, alumnos: []})

function mejorNota(curso: Curso) {
    // La destructuración no sólo funciona con hashes.
    const [{ nota }, ...otros] =
        // Podemos poner alias a las referencias obtenidas.
        curso.alumnos.sort(({ nota: notaA }, { nota: notaB }) => notaA - notaB)

    // Podemos insertar un campo que se llama igual que su variable sin usar el nombre.
    return { curso, mejorNota: nota }
}
```

Es importante notar cómo trabajar sobre estructuras mucho menos complejas y eficientes que otros lenguajes abre las puertas a una manipulación más dinámica y menos verbosa. También es interesante tomar esta interfaz como ejemplo de un lenguaje de propósito general evolucionando para adaptarse mejor al propósito específico para el que se lo usa. Si lo que vamos a hacer con *ES* es manipular *JSON*, porqué no darle las mejores herramientas posibles para eso (sacrificando otras cosas).

Por último, cabe mencionar que ninguno de los dos enfoques es especialmente bueno para realizar transformaciones anidadas o complejas, con lo que no es raro que existan librerías para ambos lenguajes que implementan **[Lenses](http://www.haskellforall.com/2012/01/haskell-for-mainstream-programmers_28.html)** (un patrón de diseño funcionaloso que apunta justamente a eso).

En esto, *Typescript* y sus *Mapped Types* son especialmente simpáticos ya que permiten tipar un contrato dinámico en lugar de tener que basarlo en strings como suele hacerse (aunque no pudimos encontrar una implementación que lo haga y así que tubimos que hacer la nuestra...):

```typescript
type Lens<T, U> = { [K in keyof U]: Lens<T, U[K]> } & { (t: T, u: U): T, (t: T): U }

// Más adelante explicamos esta implementación. Por ahora no importa...
function lens<T,U>(path:string[]): Lens<T, U> {
    const f = ((t: T, u: U) => t) as Lens<T, U>
    
    return new Proxy(f, {
        get<K extends keyof U>(_, k: K) { return lens<T, U[K]>([k, ...path]) },
        apply(_, self, args): T {
            if (args[1]) {
                const [k, ...ks] = path
                const clone: T = { ...args[0] }
                let current: any = clone
                ks.reverse().forEach(k => {
                    current[k] = { ...current[k] }
                    current = current[k]
                })
                current[k] = args[1]
                return clone

            } else return path.reverse().reduce((e, k) => e[k], args[0]) as T
        }
    })
}

function $<T>() { return lens<T, T>([]) } 


const $curso = $<Curso>()
// Noten que tipa bien y hasta funciona el autocompletar!
const $nombreDeCurso = $curso.materia.nombre

const unCurso: Curso = { materia: { nombre: "tadp"} }
const otroCurso = $nombreDeCurso(unCurso, "taDEp")

console.log($nombreDeCurso(unCurso))
```


## Funciones como elementos de primer órden

Como ya deslizamos antes, ambos lenguajes permiten definir tanto **Closures** (**Bloques**, **Lambdas**, etc.) como **Funciones Nombradas** independientes a cualquier objeto.

- (T) arrows
- (T) los objetos no pueden ser funciones

- (K) Funciones sueltas
  it (nombre implicito del primer parámetro)
  lambdas / closures / funciones anónimas
  referencias a funciones y properties (::f)
  referencias a constructores (acá es interesante que el constructor tiene tipo () -> T, es algo que javascript hacía hace mil )
  bound functions: obj::f
- invoke (apply)

- aplicación parcial y currificación (simulado ~> cambia la firma)
- composición de funciones (tal vez mostrar lo de Elm?)
- orden superior en los contratos
  - comparar con lo que hace Java y con lo de scala (básado en un contrato)
  (T) interfaz del array (map, filter, reduce)

- (T) propuestas para bind y pipe:
  https://github.com/Microsoft/TypeScript/issues/17718
https://github.com/Microsoft/TypeScript/issues/3508

## Pattern Matching y Control de Flujo

- para pensar: cómo subsanaron algunos lenguajes el conflicto entre polimorfismo y pattern matching? Bueno, algunos lo descartaron como control de flujo, pero se quedaron con la idea de patrón y deconstrucción.

- (T) Type guards, aftercheck y user defined guards / (K) Smart-cast, is, !is, as, as? <- es interesante pensar que acá el "fallo silencioso" básicamente retorna una mónada.
- (K) when operator for pattern matching like thinguies (kotlin no tiene PM)
- (K) brake y return con labels (GOTO!?)


## Mónadas y Secuenciamiento

- (T) generators
  cosas locas del yield
  yield + promises?
- (T) Promise
  async / await
- (K) Null Safety
  T?
  obj?.m()
  obj!!.m()
  obj?.let {  } //map
  ?:
  Impacto en el sistema de tipos: No es sólo un azucar, Any <: Any?, mientras que Maybe[Any] no tiene nada que ver con Any. A su vez, en Kotlin null no es subtipo de todo.
- (Elm) Maybe, List (pero no monads?)

## Reflection

- (T) Reflect
- (T) Proxy
- (T) Decorators / (K) annotations


## Expresividad (no es un buen nombre para esta sección)

- (T) atributos en base al nombre de la variable
- (T) nombres computados
- (T)(K) Type Alias
- (K) Delegation
  Class delegation
  Delegated properties (para lazyness, observers y otras yerbas (decorator?) )
  Lazy & Observable (standard delegate)
- (K) Qualified this (this@A)
- (K) Extension functions (extensión no invasiva)
    Extensiones al companion object
- (K) open classes y methods para poder extender…
- (K) Data classes (case classes)



---------------------------
---------------------------
COSAS SUELTAS
---------------------------
---------------------------

Cosas piolas para pensar:
Los lenguajes se ajustan al nivel de abstracción de los problemas que trabajan.
Cómo afecta cuando trasladas un poblema del código a un dsl. O al lenguaje. O al compilador (onda puerta abierta a macros).

```diff
- esto no está bueno
+ esto sí
```

## Otros lenguajes interesantes

ELM ( http://elm-lang.org/docs/syntax, https://guide.elm-lang.org/, http://elm-lang.org/examples/pipes)
Haskell cagon.
orientado al frontend (domain-specific)
Basado en JS. pero NO TIENE OBJETOS (records sin "this")
enforced semantic versioning
Pipes ( |> ): combinadores de funciones (como composición)
No user-defined type classes / no polimorfism ad-hoc (http://faq.elm-community.org/#does-elm-have-ad-hoc-polymorphism-or-typeclasses)
Referenciar accessors como funciones ```points.map(function(p) { return p.x })``` es ```List.map .x points```


Julia (https://docs.julialang.org/en/stable/)
sin objetos?
Vectorized operators (lifteado de operadores para el array)
promotion and convertion


Rust (https://www.rust-lang.org/en-US/documentation.html)
Bajo nivel (punteros y memoria)
pattern matching
mutabilidad controlada
“simula” objetos con una sintáxis para fn en los struct


Earlang(https://www.erlang.org/)
Más de lo mismo. Estructurado funcionaloso
Patter matching, estructuras basicas
