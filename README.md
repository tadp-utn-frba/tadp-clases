# Objeto-Funcional en otros Lenguajes

La idea de esta clase es plantear cómo se están encarando los enfoques que presentamos en clase en otras técnologías y ver de paso algunas herramientas y nociones nuevas que giran alrededor de problemas similares. En particular vamos a presentar 2 lenguajes que son, a nuestro entender, los exponentes más interesantes de esta segunda generación de tecnologías Objeto-Funcionales: **[Kotlin](https://kotlinlang.org/)**, que tomó muchas ideas de *Scala* y las acomodó al mundo de Android y **[TypeScript](https://www.typescriptlang.org/)** que es hoy por hoy una de las mejores versiones tipadas de [EcmaScript](https://es.wikipedia.org/wiki/ECMAScript) (el contrato sobre el que se definen los *JavaScripts*).

Estos lenguajes van a ser importantes para nosotros no sólo por las buenas ideas a las que llegaron, sino también por sus imperfecciones, los problemas a los que están sujetos, las limitaciones que sus diferentes contextos les imponen y la manera (mala o buena) como decidieron sobrellevarlas. Va a ser interesante también analizar que herramientas copiaron (aunque sea como un indicador de popularidad de ciertos conceptos) y el impacto de algunas aproximaciones innovadoras que presentan a problemas viejos.

En definitiva, estos lenguajes nos importan porque no son más de lo mismo, sino que tratan (en mayor o menor medida) de darle otra vuelta de rosca a la integración de paradigmas.

-------------------------------------------------------------

## Tabla de Contenido

- [Tipado](#tipado)
- [Typescript: Tipandolo con pinzas](#typescript-tipandolo-con-pinzas)
- [Tipos Paramétricos y Varianza](#tipos-paramétricos-y-varianza)
- [Features locos](#features-locos)
    - [Tipos Condicionales](#tipos-condicionales)
    - [Index Types](#index-types)
    - [Mapped Types](#mapped-types)
- [Antes y Después del Compilador](#antes-y-después-del-compilador)
- [Definición de Objetos](#definición-de-objetos)
- [Kotlin: Entre la Scala y la pared](#kotlin-entre-la-scala-y-la-pared)
- [Typescript/ES: Ahora con... Clases?](#typescriptes-ahora-con-clases)
- [Inmutabilidad y Efecto](#inmutabilidad-y-efecto)
- [Constantes](#constantes)
- [Expresiones Vs. Sentencias](#expresiones-vs-sentencias)
- [Transformación de datos inmutables](#transformación-de-datos-inmutables)
- [Funciones como elementos de primer órden](#funciones-como-elementos-de-primer-órden)
- [Pattern Matching y Control de Flujo](#pattern-matching-y-control-de-flujo)
- [Decisiones basadas en el tipo](#decisiones-basadas-en-el-tipo)
- [Control de flujo basado en valores](#control-de-flujo-basado-en-valores)
- [Mónadas y Secuenciamiento](#mónadas-y-secuenciamiento)
- [Metaprogramación](#metaprogramación)
- [Metadata](#metadata)
- [Extensiones de Interfaz](#extensiones-de-interfaz)
- [Auto-Delegación](#auto-delegación)
    - [Class Delegation](#class-delegation)
    - [Property Delegation](#property-delegation)

-------------------------------------------------------------

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

Los típos parámetricos (o, como algunos lenguajes los llaman, **Generics**) consisten básicamente en permitir parametrizar la construcción de un tipo, agregando información que puede ser usada por el chequeador para resolver situaciones complejas, donde la interfaz de un objeto depende de factores externos. Si bien la idea general es bastante sencilla, no todos los lenguajes utilizan estas herramientas del mismo modo. En clase cubrimos (casi todo) el uso que el sistema de tipos de *Scala* hace de estos parámetros y la manera en que decide cómo se relacionan los tipos en función a como se relacionan sus parámetros (**Varianza**), pero sería un error pensar que todos los lenguajes llegan así de lejos para mantener la consistencia de sus tipos. Sin ir más lejos *Java*, el punto de referencia para muchos lenguajes modernos, no maneja varianza de tipos sino que se conforma con cubrir a medias esas situaciones usando [un mecanismo de wildcards](http://www.angelikalanger.com/GenericsFAQ/FAQSections/TypeArguments.html#Topic2).

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

#### Tipos Condicionales

En typescript es posible definir un tipo en función de un chequeo de tipos:

```typescript
type Nodo = NodoNum | NodoStr
interface NodoNum { valor: number }
interface NodoStr { valor: string }

function valor<N extends Nodo>(nodo: N): N extends NodoStr ? string :
                                         N extends NodoNum ? number :
                                         never {
    return nodo.valor as any
}

const x = valor({ valor: "foo" })
const y = valor({ valor: 95 })
```

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
// El throw no es una expresión, así que NO puede usarse acá.
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

Como ya deslizamos antes, ambos lenguajes permiten definir tanto **Closures** (**Bloques**, **Lambdas**, etc.) como **Funciones Nombradas** independientes a cualquier objeto. Esto abre las puertas para trabajar al estilo de funcional (separando los datos de la lógica) sin necesidad de boilerplate o estructuras auxiliares.

**Kotlin**
```kotlin
fun main(args: Array<String>) {
// Las llaves son parte de la lambda...
val siguiente: (Int)->Int = { x -> x + 1 }

//...esto hace que la sintaxis tenga que manejar casos especiales
//para evitar parentesis redundantes.
(1..10).map{x -> x + 1}
    
//Una lambda que espera un único parámetro puede referirlo con la palabra clave "it".
val doble: (Int)->Int = { it * it }
}
```

**Typescript**
```typescript
// Puedo definir una lambda básica usando sólo el (=>).
const siguiente = n => n + 1

// La deconstrucción de parámetros funciona igual que en otras funciones.
const nota = ({nota}) => nota

// Esta sintáxis hace fácil currificar una función para aplicarla parcialmente. 
const sumar = x => y => x + y
const sumatoria = ns => ns.map(sumar(1))

// Pero esto no tiene soporte a nivel lenguaje como en Haskell... La firma cambia.
const f: (x:number, y:number) => number = sumar // Falla!
```

*Kotlin*, habiendo sido pensado originalmente para correr en *Android*, define también [algunas herramientas para usar *orden superior* de forma más eficiente](https://kotlinlang.org/docs/reference/inline-functions.html).

Además de esto, en ambos lenguajes es posible referenciar **métodos** y **properties** definidos en objetos para utilizarlos como funciones (bindeadas o no):

**Kotlin**
```kotlin
class Alumno(val nota:Int) {
    fun aprobo() = nota > 6
}

fun main(args: Array<String>) {
	val jose = Alumno(7)
	val tito = Alumno(2)
    
    val aprobo = Alumno::aprobo
    val aproboJose = jose::aprobo
    
    aproboJose() // true
    aprobo(tito) // false
    Alumno::nota.get(jose) // 7
}
```

**Typescript**
```typescript
class Alumno {
    nota: number
    constructor(nota) { this.nota = nota }
    aprobo() { return this.nota > 6 }
}

const jose = new Alumno(7)
const tito = new Alumno(2)

const aproboJose = jose.aprobo
const aprobo = Alumno.prototype.aprobo

aproboJose()             // true
aprobo.call(tito)        // false
aprobo.bind(tito).call() // false
```

Como se puede ver, en *Kotlin* el código es bastante directo (gracias al operador `::`) mientras que en *Typescript* se hace con construcciones más artesanales y asociadas a reflection.

Pese a estas facilidades, las funciones en estos lenguajes suelen quedar relegadas a usarse mayormente para parametrizar operaciones de orden superior, ya que curiosamente, al momento de escribir este documento ninguno de los dos lenguajes tiene soporte a nivel sintaxis para componer o combinar funciones, lo cual dificulta hacer construcciones complejas con ellas. Por otro lado, es cierto que en *Kotlin* esto puede modelarse fácilmente con una sintáxis muy fluida gracias a la **Sobrecarga de Operadores** y que *TypeScript* tiene [librerías que cubren esto](https://lodash.com/docs/4.17.5#flow) y algunas propuestas abiertas para incorporar **[piping](https://github.com/Microsoft/TypeScript/issues/17718)** y **[binding](https://github.com/Microsoft/TypeScript/issues/3508)**.

Finalmente, la discusión no estaría completa sin analizar el polimorfismo entre objetos y funciones. En este terreno el modelo de *Kotlin*, que define la aplición basandola en una interfaz, es ampliamente mejor que el de *TypeScript*, donde, si bien las funciones pueden tener propiedades como los objetos, son construcciones completamente diferentes y es imposible aplicar un objeto.

**Kotlin**
```kotlin
fun f(n: Int) = n + 1

object g {
    operator fun invoke(n: Int): Int = n + 1
}

fun main(args: Array<String>) {
    var aplicable: (Int) -> Int
    
    aplicable = ::f
    aplicable = g    // Esto no funciona... g no es una función como en Scala.
    
    // Pero puedo hacerlo así.
    aplicable = object : (Int) -> Int {
    	override operator fun invoke(n: Int): Int = n + 1
	}
    
    aplicable(5)
}
```

## Pattern Matching y Control de Flujo

**Patter Matching** es una construcción central del *Paradigma Funcional* pero, como tratamos en clase, favorece enfoques de diseño que abiertamente se contradicen con aquellos preferidos por el *Paradigma Orientado a Objetos*. Siendo ese el caso, no es de extrañar que varios lenguajes que pretenden soportar ambos paradigmas optan por ahorrarse la complejidad de integrar esta herramienta y proponen un desarrollo puramente basado en polimorfismo.

Si bien en la cátedra somos amigos del Pattern Matching como herramienta de diseño, hay que reconocer que, en las técnologías con *Objetos* (donde los datos son capaces de exponer su propio contenido), el matcheo de patrones sólo ofrece una ventaja sintáctica, dado que es posible obtener resultados similares con un [switch statement](https://en.wikipedia.org/wiki/Switch_statement). ¿Va a ser más feo? Sí. Pero tengan en cuenta que no es nada fácil integrar de forma consistente y robusta un mecanismo de PM a una sintáxis basada en POO. Ni siquiera *Scala*, probablemente el mejor exponente de programación hibrida *Objeto-Funcional* a la fecha, tiene una integración perfecta, ya que los patrones no están representados como **Entidades de Primer Orden**, lo cual impide utilizarlos de muchas formas interesantes (pasarlos por parámetro, retornarlos como resultado de una función, etc.).

Independientemente de si las tecnologías lo incorporan o no, es interesante analizar la discusión que instala: Hay muchas situaciones en donde puede resultar conveniente tomar una decisión basandose en la forma de una estructura y el envio de mensajes puede no ser la mejor herramienta para esto. Vamos entonces a analizar algunas variantes de herramientas que proveen los lenguajes modernos que, sin proveer un *Pattern Matching* completo, facilitan el analisis estructural para ciertas situaciones especificas.

### Decisiones basadas en el tipo

Uno de los usos más comunes de *Pattern Matching* está asociado a distinguir el tipo (en runtime) de un objeto. Este es uno de los aspectos más difíciles de emular en los lenguajes OO puros (especialmente los estáticamente tipados) dado que, sin soporte del lenguaje, incluso si averiguamos el tipo del objeto a mano, todavía tenemos que convencer al compilador de que conocemos el contenido de la variable utilizando alguna forma de casteo. Tomemos por ejemplo el siguiente código Scala, hecho sin utilizar PM:

**Scala**
```scala
trait Animal

    def aullar() = "Auuuuuu"
class Lobo() extends Animal {
}

class Vaca() extends Animal {
    def muji() = "Muuuuuuuu"
}

def haceRuido(animal: Animal): String = {
    if(animal.isInstanceOf[Lobo]) {
        // Independientemente de mi chequeo, animal debe ser casteado
        // return animal.aullar() // Esto no funciona
        return animal.asInstanceOf[Lobo].aullar()
    }
    
    if(animal.isInstanceOf[Vaca]) {
    	return animal.asInstanceOf[Vaca].muji()    
    }

    throw new Error("No hace ruido")
}
```

Algunos lenguajes modernos que reconocen la utilidad de trabajar con *polimorfismo ad-hoc* pero no soportan Pattern Matching optaron por refinar sus chequeadores de tipos para ser más sensibles al contexto. En *Kotlin* esta variante se denomina **Smart-Cast**, mientras que en *Typescript* se conocen como **Type-Guards**.

**Kotlin**
```kotlin
interface Animal

class Lobo(): Animal {
    fun aullar() = "Auuuuuu"
}

class Vaca(): Animal {
    fun muji() = "Muuuuuuuu"
}

fun haceRuido(animal: Animal): String {
    // Los chequeos de is y !is son considerados por el compilador.
    if(animal is Lobo) {
        // El bloque del if entiende que animal referencia algo de tipo Lobo.
        return animal.aullar()
    }
    
    // No sólo funciona con el if...
    animal is Vaca && return animal.muji()
    
    // Ninguno de esos mensajes puede enviarse fuera del if
    //animal.muji()
    
    throw Error("No hace ruido")
}
```

**TypeScript**
```typescript
class Lobo {
    aullar() { return "Auuuuuu" }
}

class Vaca {
    muji() { return "Muuuuuuuu" }
}

// La disjunción de tipos va a funcionar mejor que una interfaz Animal
function haceRuido(animal: Lobo | Vaca) {
    // Dentro del if sabe que es un lobo
    if (animal instanceof Lobo) return animal.aullar()

    // No hace falta chequear, si no es Lobo es Vaca...
    return animal.muji()

    // Tampoco es necesario lanzar error, sabe que no puede llegar.
    // throw new Error("No hace ruido")
}
```

En *TypeScript* el chequeo inteligente no está limitado al `instanceof`. Es posible definir nuestras propias **Type Guards** utilizando una sintaxis especial:

```typescript
function esLobo(animal: any): animal is Lobo {
    return !!(<Lobo>animal).aullar
}

function haceRuido(animal: Lobo | Vaca) {
    if (esLobo(animal)) return animal.aullar()
    return animal.muji()
}
```

Y eso no es todo; varios casos de uso comunes ya vienen soportados incluyendo lo que *TypeScript* llama **[Discriminated Unions](http://www.typescriptlang.org/docs/handbook/advanced-types.html#discriminated-unions)**:

```typescript
class Lobo {
    especie: "lobo"
    aullar() { return "Auuuuuu" }
}

class Vaca {
    especie: "vaca"
    muji() { return "Muuuuuuuu" }
}

function haceRuido(animal: Lobo | Vaca): String {
    switch (animal.especie) {
        case "lobo": return animal.aullar()
            // Si comentamos este caso (y usamos chequeo de null estricto)
            // el compilador va a avisarnos que no cubrimos todos los casos.
        case "vaca": return animal.muji()
}
    }
```

### Control de flujo basado en valores

*Kotlin* lleva estas herramientas un paso más lejos desarrollando una construcción sintáctica que, sin ser del todo *Pattern Matching* (ya que lo considera [demasiado complejo](https://discuss.kotlinlang.org/t/destructuring-in-when/2391/2)), permite analizar no solamente tipos, sino también consultar por valores específicos.

```kotlin
interface Animal

class Lobo(): Animal {
    fun aulla() = "Auuuuuu"
}

class Vaca(): Animal {
    fun muji() = "Muuuuuuuu"
}

class Camelus(val jorobas: Int): Animal

fun haceRuido(animal: Animal) =
	when(animal) {
        is Lobo -> animal.aulla()
        is Vaca -> animal.muji()
        Camelus(1) -> "Tengo 1 joroba, soy un Dromedario"
        Camelus(2) -> "Tengo 2 jorobas, soy un Camello"
        // Ojo, esto no me permite más que chequear valores específicos.
        // No tiene todo el poder del Pattern Matching!
        // Camelus(n) -> "Tengo ${n} jorobas, soy un Monstruo!" // Esto no anda!
        is Camelus -> "Tengo ${animal.jorobas} jorobas, soy un Monstruo!"
        else -> throw Error("No hace ruido")
    }
```

El `when` puede usarse también sin parámetros, como una alternativa idiomática de otras construcciones (como el `if-then-else`):

```kotlin
fun haceRuido(animal: Animal) =
	when {
        animal is Lobo -> animal.aulla()
        animal is Vaca -> animal.muji()
        // El when sin parámetro admite cualquier expresión booleana como clave
        animal is Camelus && animal.jorobas > 2 -> "Tengo ${animal.jorobas} jorobas, soy un Monstruo!"
        else -> "Soy otra cosa."
    }
```

## Mónadas y Secuenciamiento

Durante la cursada aprendimos qué son las **Mónadas**, para qué sirven, y cómo algunas de ellas (como **List**, **Option** y **Error**) contribuyen a resolver ciertos problemas recurrentes en *POO* de manera más funcional. Entonces... ¿Alguno de estos lenguajes tiene *Mónadas*?

No.

Bueno, más o menos... Si vamos a ser estrictos, una mónada no es más que un contrato que se cumple para cierta estructura y permite una forma genérica de trabajar. Desde este punto de vista, es posible implementar todas nuestras mónadas favoritas sin necesidad de soporte del lenguaje (y de hecho, ambos lenguajes tienen librerías de mónadas creadas por la comunidad).

Sin embargo, lo cierto es que si los contratos principales del lenguaje no utilizan estas mónadas de forma homogénea, es probable que su uso se haga cuesta arriba y conlleve una gran cantidad de boilerplate. Por ejemplo, el `find` en *TypeScript* retorna `null` si ningún elemento cumple la condición. Por supuesto, puedo envolver cada llamada a `find`... Pero no es lo mismo. :/

Además, hay una ventaja obvia en una sintáxis que reconoce la importancia de estas nociones (un ejemplo claro son herramientas como el **For-Comprehension** de *Scala*). El razonamiento es simple: vas a usar *mucho* esto, porqué no hacerlo lo más fácil posible? Esto es tán así que, en la práctica, es a veces más habitual encontrarse con situaciones donde es posible aprovechar los azucares sintácticos (aun sin entender del todo la teoría de fondo) que el trabajo mónadico genérico.

Y acá es dónde conviene entonces hacer una pausa y reflexionar lo siguiente: las *mónadas* pueden resultarnos interesantes por dos razones, la forma genérica de trabajar sobre ellas y las ventajas sintácticas y usos que se paran sobre esto para resolver problemas comúnes; pero no son la única forma de resolver estos problemas. Es más, pueden haber situaciones tan recurrentes o importantes que sería deseable que el lenguaje las maneje de forma especial en lugar de limitarse a aplicarles el contrato monádico.

Un buen ejemplo de esto es la posibilidad de que un valor no exista. A estas alturas de la cursada deberíamos tener este escenario asociado a la mónada **Optional** o **[Maybe](https://en.wikibooks.org/wiki/Haskell/Understanding_monads/Maybe)**), pero lo cierto es que hay otra construcción en la mayoría de los lenguajes *OO* que cumple este rol: el infame **null**. Incluso *Scala*, donde las mónadas son la propuesta por defecto para manejar esto, [hace cosas raras con sus tipos](https://stackoverflow.com/a/35848356/3871239) para darle soporte a este concepto. ¿Porqué? Bueno, por muy útil que sean los *Options*, no pueden ser usados para representar que una variable todavía no fue inicializada. Además, sin *null*, escribir código compatible con *Java* sería imposible.

Y acá es donde *Kotlin* hace algo genial a lo que llama **Null Safety**.

Básicamente, cualquier tipo `T` en *Kotlin* puede marcarse  con un `?` para indicar que la referencia apunta, o bien a un `T` o bien a `null`.

```kotlin
    // val curso: Curso = null // Falla! null no puede asignarse a Curso
    val curso: Curso? = null
```

En principio esto podría parecer similar a una *conjunción de tipos* `T || null`de *TypeScript*, pero no! En [el sistema de tipos de *Kotlin*](http://natpryce.com/articles/000818.html) el `T?` (o **Nullable Type**) **es supertipo de** `T`. Esto tiene implicaciones enormes, siendo la más obvia y directa que cualquier tipo puede ser usado donde se espera su versión *nullable*.

```kotlin
    class Curso(val docente: Docente)

    fun ojalaMeDenUnDocente(docente: Docente?) { }

    val curso: Curso = ...
    ojalaMeDenUnDocente(null) // Esto vale...
    ojalaMeDenUnDocente(curso.docente) // Esto también! No es necesario envolver al docente con un Some
```

Hasta acá todo muy lindo, pero no serviría de mucho si el `null` fuera la misma construcción tonta que en otros lenguajes. La parte más interesante de estos tipos es que vienen con su propia sintáxis para trabajarlos:

```kotlin
    // val curso: Curso = null // Falla! null no puede asignarse a Curso
    val curso: Curso? = null

    // El uso más habitual del map en un Option es enviarle un mensaje al contenido.
    // La sintaxis ?. sirve básicamente para esto.
   	curso?.docente
    
    // Este tipo de acceso puede encadenarse sin problemas.
    // Dado que no estamos envolviendo los valores en otras estructuras, T? === T??.
    // Esto quiere decir que ?. puede usarse como equivalente de map Y flatMap.
    val noEsDr: Boolean? = curso?.docente?.titulo?.startsWith("Dr")?.not()

    // Si quiero hacer algo más que mandar un mensaje al posible null puedo usar ?.let.
    // let es un mensaje que entienden todos los nullables, equivalente a map y flatMap.
    val esIncreible: Boolean? = curso?.let{ it.docente.nombre == "Marcelo" }
    
    // El ?: (operador elvis) funciona como un getOrElse.
    val esIncreiblePosta: Boolean = esIncreible ?: false
    
    // En el peor de los casos, los nullables también son considerados en el Smart-Cast.
    if(curso == null) throw Error("es null")
    // Si llega acá sabe que no puede ser null.
    curso.docente
```

Recordemos que el problema principal del `null` era que me obligaba a preguntar por él a cada paso. Esta nueva sintáxis para operar con nullables es tan buena como el map monádico (e incluso un poco menos verboso).

## Metaprogramación

No vamos a profundizar demasiado en los frameworks de **Metaprogramación** de estos lenguajes porque, en general, son bastante sencillos gracias a sus metamodelos simples y porque no distan demasiado de las formas de trabajar de otras herramientas que cubrimos en clase. Esto es especialmente cierto para [el framework de *Kotlin*](https://kotlinlang.org/docs/reference/reflection.html), que es muy similar en capacidad y diseño al de *Java*, ofreciendo una interfaz concisa y limplia de **Reflection** pero no dando casi ningún soporte para **Self Modification**. Por otro lado, en *Typescript*, como en casi todos los lenguajes dinámicos, la linea que separa el uso habitual de la *Metaprogramación* es borrosa ya que las entidades tienden a cambiar y redefinirse constantemente.

Dicho eso, podemos mencionar dos construcciones muy interesantes asociadas a la *Metaprogramación* presentes en estos lenguajes que no cubrimos durante la cursada.

### Metadata

Conocidas como **[Decorators](http://www.typescriptlang.org/docs/handbook/decorators.html)** en *TypeScript*, **Pragmas** en *Smalltalk*, **Attributes** en *.NET*, **[Annotations](https://kotlinlang.org/docs/reference/annotations.html)** en Kotlin y demas lenguajes de la *JVM* y quién sabe cuantos nombres más, estas herramientas llevan varios años siendo el standard para incorporar *Metadata* en el código (si se le puede llamar "standard" a un concepto sobre el que no nos ponemos de acuerdo ni en cómo se llama).

A grandes razgos, podemos pensar en las *Annotations* como etiquetas estáticas,generalmente parametrizables, con las que podemos marcar las abstracciones del lenguaje (properties, métodos, clases, etc.). Estas etiquetas puede ser luego consultadas a travez de una API de *Reflection* y son comunmente usadas para definir contratos que no dependan de una interfaz de mensajes.

**Kotlin**
```kotlin
annotation class Groso

class MiClase(
  @Groso val unCampoGroso: Int,
  @Groso val otroCampoGroso: String,
  val esteNo: Boolean
)

fun main(args: Array<String>){
    val atributosGrosos = MiClase::class.members
      .filter{it.annotations.any{a -> a is Groso}}
      .map{it.name}
}
```


**Typescript**
```typescript
class MiClase {
  @Groso unCampoGroso: any
  @Groso otroCampoGroso: any
  esteNo: boolean
}

function Groso<T>(target: T, key: keyof T) { 
    if(!target['camposGrosos']) target['camposGrosos'] = []
    target['camposGrosos'].push(key)
}

const camposGrosos = MiClase.prototype['camposGrosos']
```

Como se ve en los ejemplos, las *Annotations* de *Kotlin* mantienen el enfoque declarativo de *Java*, mientras que los *Decorators* de *TypeScript* son básicamente funciones destructivas que se ejecutan durante la definición. Esto los hace mucho más poderosos, permitiendo que cambien completamente la definición en la que los incluyo, pero también hace que sean más peligrosos y requiere entender exactamente qué hace cada *Decorator* que uso.

### Extensiones de Interfaz

Durante la cursada analizamos construcciones como los **Implicits** de *Scala* que permiten extender la interfaz de un objeto con nuevos mensajes sin modificarlo e incluso algunas como el **method_missing** de *Ruby* que permite que un objeto responda a mensajes cuyos nombres desconozco en tiempo estático.

Estas operaciones sólo permiten agregar métodos, pero no sobreescribirlos. Esto es así en la mayoría de las tecnologías orientadas a objetos, porque suele implementarse como un *hook* cuando falla el *method lookup* (tratar de capturar cada envío de mensaje, falle o no, es en general muy costoso).

*EcmaScript* propone una variante interesante para la extensión de los objetos: [los **Proxies**](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Proxy). Estas abstracciones no sólo permiten interceptar cualquier acceso o envío de mensaje (definido o no), sino que lo hacen de forma no-destructiva.

La mécanica es simple. Cuando quiero extender un objeto lo envuelvo con una instancia de `Proxy`, que recibe al objeto en cuestión y una configuración que le indica cómo manejar los accesos (el *proxy* redireccionará cualquier acceso al objeto interno, salvo que su configuración diga lo contrario).

```typescript
const loritoTonto = {
  decimeChau() { return 'wraaaaak' },
  cantidadDePatas: 2
}

const loritoInteligente: any = new Proxy(loritoTonto, {
  get(target, key) {
    return (key.toString().startsWith('decime'))
      ? () => key.toString().slice(6)
      : target[key]
  }
})

loritoInteligente.cantidadDePatas // retorna 2
loritoInteligente.decimeHola() // retorna Hola
loritoInteligente.decimeChau() // retorna Chau! Esto no es un method_missing!
```

Este enfoque permite capturar cualquier envío de mensaje, ya que no afecta el *method lookup* de todos los objetos, solo de los pocos proxiados (?).
**Nota:** Ya podemos volver a ver [el código de *Lenses*](###Transformación-de-datos-inmutables).

Del otro lado del espectro, en lo referente a extensiones de interfaces, *Kotlin* mantiene posiciones extrañas. Por un lado se presenta muy restrictivo, debido a [la desafortunada decisión de hacer todas las abstracciones *final* por defecto](https://discuss.kotlinlang.org/t/classes-final-by-default/166/4) que impide, entre otras cosas, extender clases que no hayan sido explicitamente marcadas como `open`; mientras que, por el otro, define unas herramientas a las que llama **Extensions**, muy similares a las **Implicit Classes** de *Scala*, que permiten extender (pero no sobreescribir) cualquier clase, abierta o no, con *métodos* y *properties*.

```kotlin
class Inutil(val nombre: String)

// No puedo extender la clase, porque no es open
class MenosInutil(nombre: String) : Inutil(nombre) {
    fun saludar() = "Hola, soy ${this.nombre}"
}

// Pero puedo "agregarle" el método...
fun Inutil.saludar() = "Hola, soy ${this.nombre}... Creo."

fun main(args: Array<String>) {
	Inutil("Ezequiel").saludar()
}
```

### Auto-Delegación

Si vamos a ser justos, esto no tiene mucho que ver con *Reflection*, pero queremos mencionarlo en esta sección porque la única forma de hacer algo parecido en la mayoría de los lenguajes requeriría de *Metaprogramación* de algún tipo.

Básicamente, *Kotlin* identifica dos situaciones comunes cuya solución suele ser trivial, pero engorrosa y plagada de boilerplate y las resuelve implementando unas construcciones sintácticas que hacen toda la mágia por atrás.

#### Class Delegation

Cualquiera que haya implementado un *[Strategy](https://en.wikipedia.org/wiki/Strategy_pattern)* sabe lo tedioso que puede resultar delegar una y otra vez mensajes en una estratégia. No es para nada infrecuente tener interfaces que consisten casi exclusivamente en reenviar mensajes al objeto correcto simplemente para ganar la flexibilidad de la composición. La **Delegación de Clases** permite hacer esto mismo sin requerir de ningún tipo de boilerplate.

```kotlin
interface Saludador {
    fun saludar(): String
}

object Formal: Saludador {
    override fun saludar() = "Tenga usted un gran día, mi buen señor."
}

object Informal: Saludador {
    override fun saludar() = "Qui hace', pa?"
}

// Persona implementa Saludador a travez de su estratégia.
data class Persona(var estrategia: Saludador): Saludador by estrategia

fun main(args: Array<String>) {
	val pepe = Persona(Informal)
    // Persona responde a la interfaz de Saludador delegando en su estratégia.
    pepe.saludar() // "Qui hace', pa?"
    
    // Ojo! El bindeo no es completamente dinámico!
    // Cambiar el atributo no cambia la delegación.
    pepe.estrategia = Formal
    pepe.saludar()  // "Qui hace', pa?"
    
    // Sin embargo, eso no es un problema si trabajamos de forma inmutable.
    val lordPepe = pepe.copy(Formal)
    lordPepe.saludar() // "Tenga usted un gran día, mi buen señor."
}
```

#### Property Delegation

De forma similar, es posible delegar la implementación de una *property* en otro objeto. Esta mecánica no se basa en extender ninguna `interface`, sino en un contrato estructural que sólo pide implementar los métodos `getValue` y `setValue`.

```kotlin
import kotlin.reflect.KProperty

class MiClase() {
    val propiedadPeresoza : String by Peresozo { "foo" }
}

class Peresozo<T>(val getter:(()->T)) {
    var valor: T? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if(valor == null) { valor = getter() }
        return valor!!
    }
}

fun main(args: Array<String>) {
    MiClase().propiedadPeresoza // Retorna "foo"
}
```

Esto tiene muchos usos prácticos, varios de los cuales ya vienen predefinidos (Ej.: [inicialización diferida](https://kotlinlang.org/docs/reference/delegated-properties.html#lazy), [propiedades observables](https://kotlinlang.org/docs/reference/delegated-properties.html#observable), [estado compartido](https://kotlinlang.org/docs/reference/delegated-properties.html#storing-properties-in-a-map)).
