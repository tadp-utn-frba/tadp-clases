# TADP - 2015 C2 - Clase 10 - Comportamiento vs Estructura (Orden Superior), Monadas y Monoides

## Intro

Empezamos repasando la solución a la que habíamos llegado la clase pasada para el ejercicio del microprocesador:

[https://github.com/uqbar-paco/tadp-2015c2-clase9-microprocesador/tree/master/funcional-inmutable
](https://github.com/uqbar-paco/tadp-2015c2-clase9-microprocesador/tree/master/funcional-inmutable)

Teníamos una solución que ejecutaba el programa recursivamente, nuestros objetos eran inmutables y para manejar los resultados (tanto normales como excepcionales) teníamos distintos tipos de resultados de ejecución (Ejecutando, Halt y Error) que contenían al micro en el estado correspondiente a ese punto de la ejecución.

Si en algún momento se llegaba a un resultado que no fuera Ejecutando, la recursión terminaba retornando ese último estado alcanzado, y también se cortaba la recursividad al llegar a la lista vacía lógicamente.

## Orden Superior

El siguiente paso que queremos dar es eliminar el algoritmo recursivo cambiándolo por una abstracción de orden superior que es la reducción (o fold), y de esa forma poder olvidarnos del manejo estructural de la lista de instrucciones y sólo pensar cómo pasar del estado de ejecución anterior al siguiente en base a la instrucción a ejecutar:

[https://github.com/uqbar-paco/tadp-2015c2-clase9-microprocesador/tree/master/funcional-fold](https://github.com/uqbar-paco/tadp-2015c2-clase9-microprocesador/tree/master/funcional-fold)

Vemos cómo el matcheo de la lista vacía para retornar un resultado de ejecución Ejecutando con el micro en el estado en el cual nos haya llegado por parámetro se vuelve la semilla del foldeo.

Otra diferencia destacable entre la solución recursiva y la que usa fold es que esta nueva solución no va a dejar de trabajar sobre el resto de la lista si se llega a un resultado de Halt o Error, cosa que sí sucedía anteriormente, sino que va a procesar la lista entera (porque eso es lo que hace el fold) motivo por el cual la función que le pasamos se encarga de ignorar la ejecución de la instrucción recibida si el resultado anterior era un Error o Halt.

Esta solución todavía no es del todo satisfactoria, pero antes de dar el paso siguiente, vamos a tratar de reentender las listas y las funciones de orden superior para trabajar sobre ellas en términos más abstractos.

Una lista es un tipo algebraico que tiene dos formas: Nil (lista vacía) y x::xs (con cabeza y cola, cuya cola es una lista), estas formas pueden usarse para trabajar mediante pattern matching. Además viene con un set de funciones básicas, a partir de las cuales se puede construir todo lo demás: head, tail e isEmpty.

Lo que nos va a interesar por un rato es pensar a la lista como una cajita que sirve para contener otra cosa, y no pensar en los detalles estructurales. La caja cerrada podría o no tener contenido, al usar pattern matching o funciones básicas como las mencionadas anteriormente lo que hacemos es abrir la caja para saber si tiene algo y, en ese caso, trabajar con su contenido.

Sin embargo al usar funciones de orden superior podemos delegar el trabajo de revisar la caja a alguien más y sólo pensar en términos del contenido cuando eso es lo que realmente nos interesa.

Entonces supongamos que tenemos una función f cuyo dominio es de tipo A, y lo que tenemos es un valor de tipo A, podemos usar esa función f directamente sobre dicho valor. Sin embargo, si tenemos una caja cuyo contenido es de tipo A, no vamos a poder usar esa función f sobre la caja, pero podríamos usar una función de orden superior que sepa trabajar con la caja para aplicar f sobre su contenido.

Definimos en estos términos qué es lo que hacen las operaciones de orden superior que nos van a interesar:

- El map: dada una cajita[A] y una función A => B, abre la cajita, aplica la función al contenido y retorna una cajita[B].

- El filter: dada una cajita[A] y una función A => Bool, abre la cajita, elige el contenido en base a la función y retorna una cajita[A].

- El flatmap: dada una cajita[A] y una función A => cajita[B], transforma el contenido como el map y además junta el contenido de la/s cajita/s que obtuvo aplicando la función en una cajita[B] y retorna esa cajita.

- El fold: dada una cajita[A], una función para reducir a algo de tipo B y una semilla de tipo B, me retorna, no una cajita, sino un B! Lo que nos va a interesar de esta operación al pensar en cajitas es que es la única operación que rompe la cajita, dejándonos decidir cómo hacerlo, no sólo por la función que recibe sino porque la semilla sirve para saber qué retornar cuando la cajita no tiene contenido.

En el fold nos importa saber lo que hay adentro, en el resto no, porque llega una caja cerrada y sale una caja cerrada. En fold sale un elemento.

Al usar estas operaciones, en ningún momento nos interesa si la caja realmente tiene o no contenido, podemos pensar siempre para el caso en el que sí lo tiene y sólo preocuparnos por el caso en el que no cuando necesitamos romper la caja. Si tenemos una secuencia de operaciones sobre el contenido de la caja, no hace falta trabajar con pattern matching en cada paso, las mismas operaciones se encargan de decidir qué ejecutar y qué no en base a la forma de la caja que haya llegado, con lo cual podemos pensar siempre en el caso positivo en el cual la caja tiene contenido para poder secuenciar el comportamiento que queremos.

La ventaja de poder trabajar de esta forma es análoga a la que se obtiene al usar excepciones adecuadamente en vez de códigos de error como valores de retorno de un envío de mensaje. En vez de tener que checkear luego de mandar ese mensaje por el código de error para ver si se puede continuar con la siguiente operación o no, podemos asumir que funcionó correctamente para seguir trabajando y esperar que en el caso contrario la operación tire un error que burbujee hasta un punto en el cual sepamos cómo recuperarnos del mismo.

Si el resultado de un filter era una cajita sin contenido, y hacemos un map directo sobre el resultado del filter, obtendremos otra cajita sin contenido. Es intuitivo y no tenemos por qué preocuparnos al respecto.

Ahora que ya tenemos la intuición sobre estas operaciones en término de contenedores, introducimos dos nuevos tipos de contenedores muy comunes que también pueden trabajarse con estas operaciones.

## Option

Option es otro tipo de cajita que sirve para cuando podría o no haber un valor como resultado de una operación. Por ejemplo:

~~~scala
list.find(_.esAzul)
~~~

Tal vez haya algo que cumpla con ser azul, o tal vez no. Por ese motivo no se puede simplemente hacer:

~~~scala
list.find(_.esAzul).dameAlgo
~~~

...porque qué pasa si no había ningún azul? no siempre voy a poder obtener algo a lo que le pueda mandar el mensaje que quiero.

El tipo del find es: find(p: (A) ⇒ Boolean): Option[A]

Que significa que puede retornar o bien Some(valor:A) o None. A partir de eso podríamos hacer algo como esto:

~~~scala
list.find(_.esAzul) match {

 case Some(azul) => Some(azul.dameAlgo)

 case None => None

}
~~~

O sino usando la funcionalidad básica de Option que son isEmpty y get (que en caso de ser None explota):

~~~scala
val resultado = list.find(_esAzul)
if(!resultado.isEmpty) Some(resultado.get.dameAlgo) else None
~~~

Pero como Option es un contenedor, también podríamos secuenciar este comportamiento usando las operaciones de orden superior mencionadas anteriormente:

list.find(_.esAzul).map {_.dameAlgo}

De las 4 operaciones de orden superior que vimos, la única que varía un poco entre List y Option es el fold. Para poder romper la caja siendo una lista necesito reducir todos los elementos que contiene a uno solo, y para lograr eso la función de reducción espera dos parámetros. En el caso del Option, como a lo sumo contiene un valor, romper la caja en el caso de ser un Some dependerá exclusivamente de dicho valor. Por ese motivo cada estructura tiene un fold a su medida, List define:

~~~scala
foldLeft[B](z: B)(f: (B, A) ⇒ B): B
foldRight[B](z: B)(op: (A, B) ⇒ B): B
~~~

mientras que Option define:

~~~scala
fold[B](ifEmpty: ⇒ B)(f: (A) ⇒ B): B.
~~~

Por si no me interesa realizar una transformación sobre el valor del Option, en vez de usar fold con una función identidad se puede usar getOrElse que también nos permite romper la caja pasando el valor alternativo en caso de que sea un None.

Por ejemplo, tenemos una jaula (que sería nuestra caja) y adentro una golondrina viva (o no).

Option toma dos valores, None y Some.

Si digo Option[Golondrina] puedo tener cualquiera de los dos, pero lo importante es que sigue saliendo un Option.

En un caso en el que un map nos devolviese un Option (caja), con FlatMap, en vez de devolvernos una caja, nos va a dar un valor: Some o None.

Al fold hay que pasarle la semilla, porque si la caja que le mando es un None es lo que voy a devolver. Y la funcion, va a servir si tengo un Some.

De las cajas no sabemos su contenido, pero sí sabemos su tipo.

### Try

El Try es una cajita que nos permite encapsular una excepción o el resultado de una operación exitosa, y permite secuenciar comportamiento sin tener que detenernos a ver si falló o no. A su vez nos permite mantener la información de la causa del problema.

Try tiene dos formas: Success y Failure, los mensajes básicos de acceso para esta estructura son isSuccess, isFailure y get.

Cabe destacar que en el caso de que se haga un filter sobre un Success y el filtrado falle por no cumplirse la condición, lo que se retorna es un Failure con una excepción de tipo NoSuchElementException. De esa forma cualquier comportamiento sobre el Try secuenciado luego de un filtrado fallido no se ejecutará.

Además tiene definidos mensajes propios como:

~~~scala
transform[U](s: T => Try[U], f: Throwable => Try[U]): Try[U]

getOrElse[U >: T](default: => U): U

orElse[U >: T](default: => Try[U]): Try[U]
~~~

Y a su vez podemos obtener un Option a partir de un Try (a costa de perder la información del error en caso de que fuera un Failure, ya que el mismo se transforma en un None) mandándole el mensaje toOption.

Try entiende map, flatmap y filter pero no fold, pero ya vimos que hay otra forma de romper la caja y no necesariamente debe ser polimórfico respecto a ese mensaje.

Estas cajitas contenedoras son denominadas Mónadas (o Monoides para en el caso de Try, ya que no cumple con todas las propiedades de las Mónadas). Una construcción propia puede también implementar el comportamiento necesario para poder ser usado de esta forma, por eso para aprovechar estas ideas, hacemos que el ResultadoDeEjecucion, que puede ser pensado como un contenedor de un microprocesador, implemente map, flatmap, filter y fold, y cambiamos la ejecución para que se base en esos mensajes:

[https://github.com/uqbar-paco/tadp-2015c2-clase9-microprocesador/tree/master/funcional-monadas](https://github.com/uqbar-paco/tadp-2015c2-clase9-microprocesador/tree/master/funcional-monadas)

Halt y Error son estados absorbentes, entonces al intentar ejecutar una función en este estado, debería retornarse a sí mismo, mientras que el Ejecutando efectivamente debería ejecutar lo pedido y retornar el próximo resultado de ejecución.

Para la instrucción de IFNZ, como el resultado a retornar depende de las instrucciones internas, usamos un flatmap. En el caso del DIV, el resultado esperado era un Ejecutando si no se dividía por 0, de lo contrario Error, lo cual puede ajustarse a un filter.

## For Comprehension

Como la anidación de operaciones de orden superior como flatmap, map y filter es muy común y enseguida se puede volver engorroso, hay una sintaxis más amigable que permite linearizarlas que, en Scala, se llama for comprehension. En precompilación se transforman las for comprehensions en la combinación de envíos de mensajes de maps, flatmaps, filters y/o foreachs correspondientes de forma transparente. Esta sintaxis nos permite abstraernos de la cajita a la par de las operaciones que se utilizan por detrás, ya que termina resultando muy intuitivo.

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

Puede traducirse usando una sintaxis más lineal de la siguiente forma:

~~~scala
for {

  e1 <- caja

  e2 <- tx1(e1) if crit(e2)

} yield tx(e2)
~~~

Siendo que ResultadoDeEjecucion funciona actualmente basado en estos mensajes, podemos reescribir el ejecutar nuevamente pero usando for comprehension. Esta solución es mucho más simple de entender que la que manejaba los resultados con map, flatmap y filter. Sin embargo, a pesar de que no lo estemos viendo, esas operaciones se siguen usando por detrás.

Finalmente vemos que ResultadoDeEjecucion es muy similar a un Try, lo único que no se ajusta inicialmente es el tercer estado Halt, pero puede fácilmente volverse un Failure definiendo una excepción para indicar que se ejecutó un HALT. Más allá de eso, la solución que teníamos se mantiene tal cual, porque incluso para las instrucciones más complejas, las operaciones que hacíamos sobre el resultado son las mismas que hay que hacer teniendo un Try.

Pueden verse estas nuevas soluciones comentadas en

[https://github.com/uqbar-paco/tadp-2015c2-clase9-microprocesador/blob/master/funcional-monadas/src/main/scala/ar/edu/utn/tadp/microprocesador/package.scala
](https://github.com/uqbar-paco/tadp-2015c2-clase9-microprocesador/blob/master/funcional-monadas/src/main/scala/ar/edu/utn/tadp/microprocesador/package.scala)


