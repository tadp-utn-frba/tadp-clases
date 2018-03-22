# Objeto-Funcional en otros Lenguajes

 "porqué estos lenguajes"
 "links importantes a los lenguajes"
 "La idea es plantear cómo se están encarando los issues que presentamos en otras técnologías y ver de paso algunas herramientas y nociones nuevas que giran alrededor de problemas similares"

  ### Tipado

  - (T) Typesistem
    Interfaces and fullfillment
    any / void / never / null /undefined
    Type combinations (union, intersection) / (Elm) Type Union
    Strings/numbers as types
    Generics
      constraints
      Bi-variance
    Optional parameters
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

  - (K) Generics
    Varianza (out / in)
    Restricciones
    Type Nothing

  - (T) No runtime types / (K) Erasure

  - (All) Inference


  ### Definición de Objetos

  - (T) Definición de objetos
    hashes
    Mixins (implementación propuesta)
    prototype
    classes
      this
      getter/setter
      super/herencia

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


  ### Inmutabilidad y Efecto

  - (T) const / (K) val, const y lateinit: no es lazy, evita el checkeo de algo que no puede ser null para hacerlo después)

  - (K) expression oriented (if, while, etc) [y fingirlo en JS con () => {}/operador ternario]


  ### Funciones como elementos de primer órden

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


  ### Pattern Matching y Control de Flujo

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


  ### Mónadas y Secuenciamiento

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

  ### Reflection

  - (T) Reflect
  - (T) Proxy
  - (T) Decorators / (K) annotations


  ### Expresividad (no es un buen nombre para esta sección)

  - (?) (T) string interpolation / (K) string template
      raw
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
  - (Elm) enforced semantic versioning



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
orientado al frontend (domain-specific)
enforced semantic versioning
Maybe, List (pero no monads?)
Union types
Pipes ( |> ): combinadores de funciones (como composición)

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
