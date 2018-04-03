# Objeto-Funcional en otros Lenguajes

##[[TODO]]
 "porqué estos lenguajes (breve descripción)"
    - Kotlin tomó mucho de Scala. Intermedio entre Java y Scala.
    - ES y qué es.
    - TS es ES tipado con cuidado de no cruzar la linea.
 "links importantes a los lenguajes"
 "La idea es plantear cómo se están encarando los issues que presentamos en otras técnologías y ver de paso algunas herramientas y nociones nuevas que giran alrededor de problemas similares"

## Tipado

Vamos a empezar planteando algunas variantes interesantes a los sistemas de tipos de los lenguajes que usamos durante la cursada. El tipado de *Scala* es de los más seguros, flexibles y, por lo tanto, complejos de los lenguajes orientados a objetos. *Kotlin* toma muchas de sus ideas y define un tipado algo más rígido y menos preciso pero mucho más simple, al mismo tiempo que agrega bastante boilerplate a su sintáxis para protejerse de (lo que algunos consideran) problemas comunes. Por otro lado, *Typescript* abiertamente acepta su tipado como **unsound** y no ofrece una solución para las situaciones más complejas que otros lenguajes tratan de resolver pagando el costo de una mayor complejidad. Del sitio de *Typescript*:

> TypeScript’s type system allows certain operations that can’t be known at compile-time to be safe. [...] The places where TypeScript allows unsound behavior were carefully considered, and throughout this document we’ll explain where these happen and the motivating scenarios behind them.

Basicamente el lenguaje aspira a que su tipado sea una mejor alternativa que el no-tipado de *EcmaScript* y está más preocupado por ser accesible que seguro. Ironicamente, esta laxedad en los chequeos permite luego tipar algunas construcciones complejas que en otros lenguajes más estrictos no serían posibles y tendrían que hacerse usando reflection u otros mecanismos inseguros.

Uno puede estar a favor o en contra de las decisiones particulares de estas tecnologías, pero hay una idea interesante escondida detrás que merece consideración: Los lenguajes (al igual que los problemas que buscan resolver) están atados a un tiempo, un público y un contexto. A veces puede ser buena idea alejarse del paradigma o implementar un concepto de forma menos (o más) rigurosa en pos de mejorar el uso cotidiano.

Vamos a mencionar entonces algunos de los aspectos más interesantes (para bien o para mal) de estos sistemas de tipos.

### Typescript: Tipandolo con pinzas

En sí, la filosofía de *Typescript* consiste en ser una versión más segura de *EcmaScript*, manteniendose fiel a sus principios y sin introducir "features" que no puedan mapearse directamente al lenguaje original.
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

Un ejemplo de esto es la **Conjunción y Disjunción de Tipos** que en *Scala* [llevan varios años discutiendo](https://contributors.scala-lang.org/t/whats-the-status-of-union-intersection-types-singleton-types-in-dotty) y *Typescript* implementa sin ningún tipo de reparo.


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

En el otro extremo del espectro, [si bien puede configurarse para hacer algunos controles básicos](https://www.typescriptlang.org/docs/handbook/release-notes/typescript-2-6.html), *Typescript* decide evitarse el problema y hacer todos los generics **Bivariantes**:

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

De más está decir que esto no es lo más seguro, pero *Typescript* elige poner la responsabilidad de evitar esos problemas en el usuario a cambio de permitirle permanecer ignorante sobre teoría de varianza y mantener el tipado suficientemente sencillo para implementar...


### Features locos

El razonamiento es simple: Desde el punto de vista del usuario, el sistema de tipos es confiable o no (no importa *porqué*). Si ya sé que tengo que estar atento cuando uso ciertas construcciones y lo acepto como parte del uso cotidiano del lenguaje, entonces es posible agregar herramientas interesantes aunque no pueda hacerlas tipar de forma completamente consistente. Vamos a mencionar un par de ejemplos de esto presentes en *Typescript*.

#### Index Types

With index types, you can get the compiler to check code that uses dynamic property names.

*Typescript* permite el mismo uso de propiedades dinámicas que *EcmaScript*. Esto incluye referenciar el nombre de propiedades con construcciones no-estáticas (Ej.: obj["propiedad"] en lugar de obj.propiedad). Los [Index Types](https://www.typescriptlang.org/docs/handbook/advanced-types.html#index-types) son la construcción sintáctica que permite que el compilador analice código que usa nombres dinámicos de propiedades.

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

Tanto *Kotlin* como *Typescript* permiten algún grado de **Inferencia de Tipos**. En general, la gran mayoría de los lenguajes con tipado explicito (incluso [C++](https://dgvergel.blogspot.com.ar/2016/04/inferencia-automatica-de-tipos-auto.html)!) tratan de incorporar esto a sus sintáxis, para reducir el boilerplate y hacer más fácil la transición desde otras tecnologías. Es probable que esto se convierta pronto en el standard de la industria.

Otro patrón recurrente, un poco menos feliz, es que ambos lenguajes también decidieron descartar la información de tipos en runtime. *Kotlin*, que originalmente se compilaba para la *JVM*, pasa por el mismo proceso de **Erasure** que *Scala*, donde los tipos paramétricos se descartan post compilación.

*Typescript* va un paso más lejos y **elimina toda información sobre los tipos**, compuestos o no, para compilar al código *ES* más similar posible. Esto hace imposible usar esta información para hacer *introspection* en runtime...

Si bien existen lenguajes donde los tipos compuestos están reificados a nivel plataforma ([como .NET](https://en.wikipedia.org/wiki/Generic_programming#Genericity_in_.NET_[C#,_VB.NET])) y otros ([como *Scala*](https://docs.scala-lang.org/overviews/reflection/typetags-manifests.html)) que encontraron alguna forma de dibujarla, en general la postura suele ser que eliminar esta información es lo más rápido y fácil de hacer y evita que los programas "engorden" guardando metadata que no siempre necesitan.

De ahí que existan cosas como el [Projecto Valhalla](https://en.wikipedia.org/wiki/Project_Valhalla_(Java_language)), que aspira a agregar soporte para este y otros features a la *JVM*, lo cual podría cambiar radicalmente el modo como otros lenguajes manejan los generics.


## Definición de Objetos

- (T) Definición de objetos
  hashes
  Mixins (implementación propuesta)
  prototype
  classes
    this
    getter/setter
    super/herencia
    son funciones

- (K) clases
    companion objects <- las clases no son objetos
  Parámetros de clase
  Instanciación sin new
  Init block (similar a tirar código en el cuerpo de la clase)
  properties instead of attributes
  override de get y set más feliz (field keyword)
  open classes y methods para poder extender…

  Data classes (case classes)
  
- (K) Interfaces con código
  solución de conflictos manual
    super<C>.m

- (K) Objects (dinámicos y nombrados)


## Inmutabilidad y Efecto

- (T) const y readonly / (K) val, const y lateinit: no es lazy, evita el checkeo de algo que no puede ser null para hacerlo después)

- (K)(E) expression oriented (if, while, etc) [y fingirlo en JS con () => {}/operador ternario]

## Funciones como elementos de primer órden

- (T) arrows
- (T) los objetos no pueden ser funciones

- (K) Funciones sueltas
  it (nombre implicito del primer parámetro)
  lambdas / closures / funciones anónimas
  referencias a funciones y properties (::f)
  referencias a constructores (acá es interesante que el constructor tiene tipo () -> T, es algo que javascript hacía hace mil )
  bound functions: obj::f

- aplicación parcial y currificación (simulado ~> cambia la firma)
- composición de funciones (tal vez mostrar lo de Elm?)
- orden superior en los contratos
  - comparar con lo que hace Java y con lo de scala (básado en un contrato)
  (T) interfaz del array (map, filter, reduce)

## Pattern Matching y Control de Flujo

- para pensar: cómo subsanaron algunos lenguajes el conflicto entre polimorfismo y pattern matching? Bueno, algunos lo descartaron como control de flujo, pero se quedaron con la idea de patrón y deconstrucción.

- (T) deconstruction / (K) Destructuring
  arrays
  hashes
  objetos
  parametros de funciones:  function f({g: h}){ return h}
  default para valores no encontrados: var { a, b = 2 } = obj
  spread
- (T) Type guards, aftercheck y user defined guards / (K) Smart-cast, is, !is, as, as? <- es interesante pensar que acá el "fallo silencioso" básicamente retorna una mónada.
- (K) when operator for pattern matching like thinguies
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
