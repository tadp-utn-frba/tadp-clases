# Objetos + Funcional
Vamos a construir una solucion funcional de un dominio y luego vamos a ir introduciendo y combinando conceptos de OOP.

## Dominio
Vamos a construir un programa para analizar las pociones que se enseñan a los alumnos en el colegio Hogwarts de Magia y Hechicería, y los efectos que pueden hacer sobre las personas.

### Personas y Niveles
Una persona es una tupla de nombre y niveles.
Los niveles definen el estado de cada caracteristica de una persona: suerte, convencimiento, fuerza.

```scala
type Niveles = (Int, Int, Int)
type Persona = (String, Niveles)

val personas = List(
  ("Harry", (11, 5, 4)),
  ("Ron", (6, 4, 6)),
  ("Hermione", (8, 12, 2)),
  ("Draco", (7, 9, 6))
)
```

### Efectos
Los efectos son funciones que reciben niveles y devuelven el nuevo estado de los niveles.

```scala
type Efecto = Niveles => Niveles
```

#### Aplicación Parcial y Currificación

Definamos un efecto que duplique todos los niveles:

```scala
def duplica(niveles: Niveles) = niveles._1 * 2 + niveles._2 * 2 + niveles._3 * 2
```

Si ahora queremos definir otro efecto que se quede con el máximo valor entre cada nivel y 7, tendríamos que duplicar la lógica de la función anterior en gran parte.

Definamos entonces una función que dada una operación, la aplique a cada nivel:

```scala
def mapNiveles(f: Int => Int, niveles: Niveles) =
  (f(niveles._1), f(niveles._2), f(niveles._3))

val duplica: Efecto = mapNiveles(_ * 2, _)

val alMenos7: Efecto = mapNiveles(_.max(7), _)
```
Estamos usando el "_" para aplicar parcialmente una función.

Aplicar parcialmente las funciones es muy importante porque me deja transformarlas en otras funciones más específicas, para componerlas o pasarlas por parámetro.

Una función en Scala puede recibir multiples grupos de parámetros. Dado que en Scala las funciones no están completamente currificadas, uno tiene que declarar "grupos de aplicación" para poder evitar el "_".

Entonces podemos reescribir la función anterior como:

```scala
def mapNiveles(f: Int => Int)(niveles: Niveles) =
  (f(niveles._1), f(niveles._2), f(niveles._3))

val duplica: Efecto = mapNiveles(_ * 2)

val alMenos7: Efecto = mapNiveles(_.max(7))
```

## Composición de Funciones

En el paradigma funcional es importante componer porque las funciones son los ladrillitos con los que construiamos los programas.

En funcional las funciones son chiquitas y cohesivas. Las combinamos entre ellas para construir algoritmos más grandes.

Por ejemplo, en haskell es natural hacer algo como:

    (length . filter aprobado . map parcial) alumnos

Tenemos las funciones length, filter y map (sin contar las que usamos cómo parámetro) y las combinamos en secuencia para hacer algoritmos más complejos.

En el paradigma orientado a objetos hacemos:

    alumnos.map(_.parcial).filter(_.aprobado).length

Cual es la diferencia?

En funcional cada uno tiene que construir las operaciones por afuera de los datos, en objetos los mismos datos pueden proveer las funciones. No necesitamos componer, porque podemos mandarle mensajes al resultado de una operación.

Creemos las siguientes funciones y veamos como funciona la composición:

- Sumar el valor de todos los niveles

```scala
val toList: Niveles => List[Int] = niveles => List(niveles._1, niveles._2, niveles._3)
val sumaNiveles: Niveles => Int = toList.andThen(_.sum)
```

- Calcular la diferencia entre el nivel más alto y el más bajo

```scala
val maxNivel: Niveles => Int = toList.andThen(_.max)
val minNivel: Niveles => Int = toList.andThen(_.min)
val diferenciaNiveles: Niveles => Int = niveles => maxNivel(niveles) - minNivel(niveles)
```

- Sumar los niveles de una persona

```scala
  def niveles(persona: Persona) = persona._2
  val sumaNivelesPersona: Persona => Int = sumaNiveles.compose(niveles)
```

Podemos ver que las funciones pueden ser compuestas usando "andThen" y "compose" para crear nuevas funciones más complejas.

### Pociones
Una poción es una tupla de nombre y lista de ingredientes.
Los ingredientes son una 3-upla de nombre, cantidad del ingrediente y lista de efectos.

```scala
type Ingrediente = (String, Int, List[Efecto])
type Pocion = (String, List[Ingrediente])

// Pociones
val multijugos = ("Multijugos", List(
  ("Cuerno de Bicornio en Polvo", 10, List(invierte, suerteEsConvencimiento)),
  ("Sanguijuela hormonal", 54, List(duplica, suerteEsConvencimiento))
))

val felixFelices = ("Felix Felices", List(
  ("Escarabajos Machacados", 52, List(duplica, alMenos7)),
  ("Ojo de Tigre Sucio", 2, List(suerteEsConvencimiento))
))

val floresDeBach = ("Flores de Bach", List(
  ("Rosita", 8, List(duplica))
))

val pociones: List[Pocion] = List(felixFelices, multijugos, floresDeBach)
```

#### Funciones Parciales

Decimos que una poción es "heavy" cuando al menos tiene 2 efectos. Obtengamos una lista de todas las pociones heavies.

```scala
def efectos(ingrediente: Ingrediente) = ingrediente._3
val todosLosEfectos: List[Ingrediente] => List[Efecto] = _.flatMap(efectos)
val ingredientes: Pocion => List[Ingrediente] = _._2
val efectosPocion: Pocion => List[Efecto] = todosLosEfectos.compose(ingredientes)
val esHeavy: Pocion => Boolean = efectosPocion(_).size >= 2
def nombre(pocion: Pocion) = pocion._1
val pocionesHeavies: List[Pocion] => List[String] = _.filter(esHeavy).map(nombre)
```

Pero también podemos usar pattern matching y funciones parciales para conseguir lo mismo:

```scala
val pocionesHeaviesPartial: List[Pocion] => List[String] = _.collect {
  case (nombre, ingredientes) if todosLosEfectos(ingredientes).size >= 2 => nombre
}
```

Lo que acabamos de definir es una función parcial, una función que no está definida para todos los valores de su dominio.

Scala provee un azucar sintáctico para escribir funciones parciales con una sintaxis identica a la del patern matching sin iniciar con el "match".

En caso de querer definir una funcion parcial con la misma sintaxis, estamos obligados a hacerlo en una variable o parámetro que esté tipado explicitamente.

```scala
val nombreDePocionHeavy: PartialFunction[Pocion, String] = {
  case (nombre, ingredientes) if todosLosEfectos(ingredientes).size >= 2 => nombre
}
```

Las funciones parciales son un tipo particular de "Function1[A, B]" o "A => B". Ellas extienden la interfaz de las funciones y le agregan más comportamiento.

Que pasa si a una función parcial la evalúo con un valor para la cual no está definida? Lanza una excepción (MatchError).

Para evitar esto puedo utilizar los métodos que me provee, como por ejemplo:

Puedo preguntar si una funcion parcial está definida para un valor:

```scala
nombreDePocionHeavy.isDefinedAt(felixFelices) // true
nombreDePocionHeavy.isDefinedAt(floresDeBach) // false
```

Puedo pasarle una función de fallback:
```scala
val nombreDePocionConFallback = nombreDePocionHeavy.orElse {
  case (nombre, _) => s"$nombre no es heavy"
}
nombreDePocionConFallback(felixFelices) // Felix Felices
nombreDePocionConFallback(floresDeBach) // Flores de Bach no es heavy
```

Y puedo transformarla en una función que si esté definida para todo su dominio (cambia el tipo de retorno a Optional usando None para los valores no definidos):

```scala
nombreDePocionHeavy.lift(felixFelices) // Some("Felix Felices")
nombreDePocionHeavy.lift(floresDeBach) // None
```

Ya (abu)usamos funciones parciales anteriormente para utilizar la deconstrucción por patrones y hacer más facil la definición de una función. Veamos como escribir de diferentes maneras el efecto "invierte":

```scala
val invierte1: Efecto = niveles => (niveles._3, niveles._2, niveles._1)
val invierte2: Efecto = {
  case (a, b, c) => (c, b, a)
}
```

Como las funciones parciales también son funciones, la definición anterior es totalmente valida y dado que estamos incluyendo todos los valores del dominio, esas funciones son análogas.