# TADP - 2015 C2 - Clase 10 - Comportamiento vs Estructura (Orden Superior), Monadas y Monoides


## Orden Superior

Recordamos las Listas de Haskell, cómo estaban definidas, cómo podíamos trabajarlas por recursividad y que, de ser posible, resultaba mucho más práctico utilizar **Operaciones de Orden Superior**, que encapsulaban la idea de una operación y permitían que pensemos nuestros programas de forma más **declarativa**, con un nivel de **abstracción** más cercano al problema que teníamos que resolver.

Recordamos algunas operaciones como *filter*, *map*, *flatMap* y *fold*, qué tipo tenían, cómo se usaban y cuál es la intuición de las abstracciones que encapsulan. El objetivo es entender que estas operaciones pueden ser pensadas de forma independiente a los detalles de implementación de la lista; para eso tratamos de olvidarnos de las listas y pensar las operaciones en terminos de "cajas":

- filter: dada una caja[A] y una función A => Bool, abre la caja, toma de su contenido los elementos que pasan la función criterio y los pone en una nueva caja[A].

- map: dada una caja[A] y una función A => B, abre la caja, transforma el contenido usando la función y retorna el resultado en una nueva caja[B].

- flatmap: dada una caja[A] y una función A => **caja[B]**, abre la caja, transforma el contenido usando la función, **abre estas nuevas cajas** y retorna su contenido en una única caja[B].

- El fold: dada una caja[A], y algún mecanismo que me permita convertirla en un valor B, aplica dicho mecanismo para abrir la caja.

El fold va a ser un caso particular, dado que es la única de estas operaciones que **no retorna una nueva caja**. Todas las otras reciben y devuelven cajas, con lo que pueden componerse libremente, pero el fold abre la caja y rompe la cadena.

Señalamos también que, al usar estas operaciones, en ningún momento nos interesa si la caja realmente tiene o no contenido. Podemos pensar nuestro programa secuenciando operaciones y sólo preocuparnos por el caso de que no haya contenido cuando necesitamos abrir la caja. Por ejemplo, para el siguiente código:

~~~scala
  caja.filter(f).map(g).filter(h).flatMap(i).filter(j).size > 2
~~~

No tiene ninguna importancia si el resultado de alguno de los filter (o la caja inicial) es una caja sin contenido, porque **nunca necesitamos acceder al contenido**.

Ahora que ya tenemos la intuición sobre estas operaciones en término de contenedores, introducimos dos nuevos tipos de contenedores muy comunes que también pueden trabajarse con estas operaciones: Option, que modela la posibilidad de tener o no algo, y Try, que representa la posibilidad de obtener un valor o fallar en el intento. Pueden parecer similares, pero sus estructuras y finalidades son distintas.

Vemos entonces que el Option es un gran sustituto del null (y además es type-safe!) y el Try es excelente para reemplazar el manejo de excepciones con valores de retorno. También vemos que con el Try podemos desentendernos de cuando ocurre un error hasta que podamos manejarlo (similar a lo que pasaba con manejo de excepciones).

De las 4 operaciones de orden superior que vimos, la única que varía un poco entre List, Option y Try es el fold, justamente porque cada caja debe abrirse pensando en cada una de sus posibles formas: Distintas cajas se abren distinto.

La parte mágica de todo esto está en notar que es posible aprender a trabajar en un nivel de abstracción por encima de los datos. Podemos desentendernos de la forma de la caja estamos usando y simplemente pensar en termino de combinar filtrados, mapeos y otras operaciones. (Así como antes decíamos "quiero mapear, no me importa si la lista está vacía o tiene elementos", ahora podemos decir "quiero mapear, no me importa ni siquiera si es una lista" :p ).


## Monadas y Monoides

Podemos, de forma más bien laxa, llamar a estas cajas **Monadas** (o **Monoide** para el caso del Try), nombre que Scala toma del paradigma funcional y para el cual hace una implementación un tanto diferente a la de Haskell. No vamos a meternos a explicar en detalle qué es una Monoide o que propiedades tiene que cumplir para considerarse una Monada. Basta con entender que, así como la composición de funciones es un mecanismo para secuenciar operaciones, las Mónadas son un mecanismo para secuenciar tranformaciones de datos. Si quieren profundizar sobre el tema pueden encontrar un par de buenos artículos [acá](https://wiki.haskell.org/Monad) y [acá](https://medium.com/@sinisalouc/demystifying-the-monad-in-scala-cc716bb6f534).



## For Comprehension

Como la anidación de operaciones de orden superior como flatmap, map y filter es muy común y enseguida se puede volver engorroso, hay una sintaxis más amigable que el compilador reescribe por atrás a operaciones monádicas. En Scala, esta sintaxis se llama **for-comprehension** y está inspirada en la *do-comprehension* de Haskell.

En precompilación se transforman las for-comprehensions en una combinación de maps, flatmaps, filters y/o foreachs. Esta sintaxis nos permite abstraernos de la cajita a la par de las operaciones que se utilizan por detrás, y termina resultando muy intuitivo.

Por ejemplo, una sentencia compleja como la siguiente:

~~~scala
caja.flatmap { e1 ->
  tx1(e1).flatmap { e2 ->
    tx2(e2).filter { e3 ->
      crit(e3)
    }.map { e4 ->
      tx3(e4)
    }
  }
}
~~~

Puede escribirse así:

~~~scala
for {
  e1 <- caja
  e2 <- tx1(e1) if crit(e2)
} yield tx(e2)
~~~

El compilador va a tomar la expresión del segundo bloque de código y a convertirla en lo que se ve en el primero.


## Bajando a Tierra

Por último, en el siguiente ejemplo vamos a tratar de aprovechar estas nuevas ideas para mejorar la ejecución en nuestro Micro; primero convirtiendo los resultados de ejecución en Monoides y luego descartandolos en favor de utilizar un Try.

[repo funcional-monadas](https://github.com/tadp-utn-frba/tadp-clases/tree/scala-microprocesador-tuneado/funcional-monadas)
