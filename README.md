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

Decimos que una poción es "heavy" cuando al menos tiene 2 efectos. Obtengamos una lista de todas las pociones heavies.



## Funciones parciales
Contar funciones parciales, orElse().

#### Funciones Parciales

Creemos un efecto que solo está definido para algunos de los posibles niveles, no para todos. Queremos un efecto que copia la suerte como valor del convencimiento si "suerte > convencimiento":

```scala
val suerteEsConvencimiento: Efecto = {
  case (suerte, convencimiento, fuerza) if suerte > convencimiento => (suerte, suerte, fuerza)
}
```

Lo que acabamos de definir es una función parcial, una función que no está definida para todos los valores de su dominio.

Porqué si definimos a los efectos como funciones, asignar una función parcial compila perfectamente? Por que las funciones parciales **son** funciones, en particular son "Function1[A, B]" o "A => B".

Ellas extienden la interfaz de las funciones y le agregan más comportamiento como:

```scala
def isDefinedAt(x: A): Boolean
def lift: A => Option[B]
def orElse(that: PartialFunction)
```

Que pasa si evalúo el efecto anterior para un valor de "suerte" <= "convencimiento"? Nos lanza una excepción "MatchError"!

Definamos un fallback para cuando

componer f p



También podemos (abu)usar las funciones parciales para utilizar la deconstrucción por patrones y hacer más facil la definición de una función. Por ejemplo, definamos un efecto que invierte todas las características:

```scala
// sin deconstruccíon
def invierte(niveles: Niveles): Niveles = (niveles._3, niveles._2, niveles._1)
// con pattern matching
def invierte(niveles: Niveles): Niveles = niveles match {
  case (suerte, convencimiento, fuerza) => (fuerza, convencimiento, suerte)
}
// con partial function

```




Las funciones parciales son utilizadas también para filtrar, ya que pueden decir si un valor del dominio está definido o no.
Scala collections => collect



```scala
def invierte(niveles: Niveles): Niveles = (niveles._3, niveles._2, niveles._1)
```


## Deconstrucción
## (Opcional: Implicits y Type Classes)

1) Dada una tupla de niveles definir las funciones

    a) sumaNiveles, que suma todos los niveles
    b) diferenciaNiveles, es la diferencia entre el nivel más alto y el nivel más bajo

 Dada una tupla persona definir las funciones
c) sumaNivelesPersona, por ejemplo la suma de niveles de Harry es 20 (11+5+4).
> sumaNivelesPersona ("Harry",(11, 5, 4))
20
Paradigmas de Programación Paradigma Funcional - 16/06/2010 Página 2 de 2
d) diferenciaNivelesPersona, que aplicada a Harry debería ser 7 (11 - 4).
> diferenciaNivelesPersona ("Harry",(11, 5, 4))
7
2) Definir la función efectosDePocion, que recibe una tupla poción y devuelve una lista con todos los efectos de cada
uno de sus ingredientes.
> efectosDePocion ("Felix Felices",[("Escarabajos Machacados",5,[f1,f2]),("Ojo de Tigre",2,[f3])])
[f1,f2,f3]
-–En Haskell las funciones no se pueden mostrar, esto es solo una consulta a modo de ej.
3) Definir la función pocionesHeavies, que recibe una lista de pociones y devuelve los nombres de las pociones que
tienen al menos 4 efectos.
> pocionesHeavies misPociones
["Multijugos"]
4)
a) definir la funcion incluyeA que espera dos listas, devolviendo True si la primera está incluida en la segunda. P.ej.
 incluyeA [3,6,9] [1..10]
 devuelve True
b) definir la función esPocionMagica/1. Una poción es mágica si el nombre de alguno de sus ingredientes tiene todas
las vocales y además de todos los ingredientes se pide una cantidad par.
En el ejemplo “Multijugos” es mágica, “Felix Felices” no porque ningún nombre incluye todas las vocales, “Flores de
Bach” no porque hay un ingrediente con cantidad impar.
Hint: para lo de las vocales puede convenir usar el 4.a teniendo en cuenta que los Strings son listas y que es fácil definir
una lista con las vocales.
5) Definir la función tomarPocion, que recibe una poción y una persona, y devuelve una tupla persona que muestra
cómo quedaría la persona después de haber tomado la poción.
Cuando una persona se toma una poción, se le aplican todos los efectos de cada ingrediente de la poción, en orden.
Siendo
pocionFelix =
("Felix Felices",[("Escarabajos Machacados", 52, [f1,f2]),("Ojo de tigre sucio",2,[f3])])
> tomarPocion pocionFelix ("Harry",(11, 5, 4))
("Harry", (12, 7, 12))
–-Porque le aplica f1, f2 y por último f3
--Pista: usar fold o recursividad
6) Definir la función esAntidoto, que recibe una persona y dos pociones, y devuelve true en caso de que la segunda
poción revierta el efecto de la primera sobre la persona.
Es decir, si la persona queda igual después de tomar primero la primer poción y después la segunda.
7)
Definir la función personaMasAfectada, que recibe una poción, una ponderacion de niveles y una lista de personas, y
devuelve la persona que después de tomarse la poción hace máximo el valor de la ponderación de niveles.
Una ponderación de niveles es una función que espera una terna de niveles (suerte,convencimiento,fuerza física) y
devuelve un número.
No se puede usar recursividad, listas por comprensión, foldl ni definiciones locales.
8)
Escribir consultas que, usando la función del punto anterior, respondan la persona que quedó más afectada según las
siguientes ponderaciones
a) suma de niveles (suerte, poder de convencimiento y fuerza física)
b) promedio de niveles (puede ser el promedio entero)
c) fuerza física
d) diferencia de niveles