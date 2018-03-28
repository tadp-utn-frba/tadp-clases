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
  
- (K) Generics
  Varianza (out / in)

```kotlin
class Operacion<in P,out R> {
  fun ejecutate(parametro: P): R { return null as R }
}

var f: Operacion<Int,String> = Operacion<Int,String>()
var g: Operacion<Int,Any> = f
f = g
}
```

- (T) Generics
  constraints
  Bi-variance
  Optional parameters

### Antes y Después del Compilador
- (T) No runtime types / (K) Erasure
- (All) Inference

### Cosas Raras
    Interesting types
      Readonly
      optional

  Index types
    ```function pluck<T, K extends keyof T>(o: T, names: K[]): T[K][] {
      return names.map(n => o[n]);
    }```
  Mapped types
    ```type Readonly<T> = {
      readonly [P in keyof T]: T[P];
    }```

- (K) (?) Type Nothing


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
