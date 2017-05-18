# TADP - 2015 C2 - Clase 09 - Intro Hibrido Objeto/Funcional

## Intro

Empezamos recordando los conceptos que consideramos más importantes del paradigma de objetos: **Encapsulamiento**, **Delegación** y **Polimorfismo (ad-hoc)**. Pero porqué son importantes? Qué nos dan? Hablamos de como la combinación de estas ideas hace que sea fácil agregar nuevas entidades para extender operaciones existentes, pero puede dificultar agregar nuevas operaciones (especialmente cuando no hay un único claro responsable de implementarla).

En el paradigma funcional donde las estructuras de datos están separadas de las operaciones estos conceptos parecen tener poco o ningún sentido, y son mayormente reemplazados por nociones como **Transparencia Referencial** (que implica falta de *efecto colateral* y, por lo tanto, *inmutabilidad* y conlleva la escencia de funcional: pensar los programas como transformaciones entre dominios), **Polimorfismo Paramétrico** y **Orden Superior** (termino que usamos de forma laxa, para referirnos al uso de las operaciones como individuos de primer orden).

Es fácil ver que estas ideas se contraponen: El *Polimorfismo Paramétrico* depende de conocer desde la operación la forma de los datos, lo cual choca con la idea de *Encapsulamiento*. Separar los datos de las operaciones favorece el *orden superior*, pero hace imposible *delegar* en los datos y compromete el *polimorfismo ad-hoc*; sin embargo también hace más sencillo agregar nuevas operaciones, aunque se complica agregar nuevas estructuras.

Visto así, no parece buena idea mezclar estos dos mundos, hasta que empezamos a entender que el Paradigma de Objetos no siempre es del todo feliz usando **Encapsulamiento**, **Delegación** y **Polimorfismo (ad-hoc)**...


## Visitor

Entra a la cancha el patrón visitor[1], el cual podemos leer del libro de Gamma. La idea es notar que este patrón, visto bajo la luz correcta, pareciera ir en contra de los conceptos principales de objetos.

Por supuesto el patrón no propone abiertamente romper estas 3 ideas, sino que las usa de tal modo que el resultado final es el opuesto al que normalmente estas buscan:

Los objetos "visitados", en lugar de implementar su própia lógica, implementan una interfaz (más bien anémica) que permite navegarlos. Los objetos "visitantes" representan una operación que normalmente sería parte de la interfaz de los visitados, quedando fuertemente acoplados a estos, pero permitiendo agregar nuevas operaciones de forma sencilla. Eso hace que los objetos visitados no puedan ser tratados polimorficamente con otros objetos que implementen dichas operaciones, dado que no exponen en su interfaz mensajes para las mismas (-Polimorfismo-); la lógica de negocio no está definida en los objetos que representan la estructura, donde normalmente estarían (-Delegación-) y su estructura queda abierta a los visitadores, que dependen de esto para poder recorrerlos (-encapsulamiento-).

El resultado final tiende a exhibir las ventajas y facilidades de extensión de la aproximación funcional (en lugar de la de objetos), aunque con el costo de ser bastante burocrático (no es raro encontrar implementaciones con *multiple dispatches* y código difícil de seguir).

Esto no quiere decir que el patrón esté mal. Al contrario, la recurrencia del problema que inspiró el patrón es un indicio de que, a veces, Polimorfismo, Delegación y Encapsulamiento pueden no ser la mejor opción.

Para ilustrar este punto podemos ver el siguiente ejercicio, en el cual se presenta para el mismo problema una implementación "pura" y una basada en el patrón visitor:

[repo objetos-puro](https://github.com/tadp-utn-frba/tadp-clases/tree/scala-microprocesador/objetos-puro)

Se puede apreciar que la solución con el visitor desacopla la lógica (operaciones como ejecutar un programa, imprimirlo o simplificarlo) de la estructura (las instrucciones del lenguaje), con lo cual es posible agregar nuevas operaciones relativamente fácil sin tener que cambiar en absoluto las instrucciones. Esto es especialmente conveniente, dado que, por la naturaleza de este problema puntual, es más probable que haya que cambiar o agregar operaciones que instrucciones.

El problema de este enfoque es que se ve muy verboso, la navegación y delegación son considerablemente más complicados y hay muchas operaciones que se vuelven más complejas por tener que adecuarse al contrato de navegación propuesto por el visitor (ej: simplify). Es dificil mejorar estos aspectos estando atados a las reglas de Objetos, pero qué tal si en lugar de tratar de usar los conceptos de objetos para obtener las ventajas de funcional rompemos el paradigma e introducimos herramientas nuevas?

De esto se va a tratar lo que queda del cuatrimestre. Vamos a tratar de tomar el mundo de objetos y el de funcional y combinarlos de forma consistente, tratando de desarrollar un criterio sobre cuando conviene usar herramientas de uno o el otro y porqué.

Empezamos entonces por introducir de a poco conceptos de funcional para tratar de mejorar nuestra solución, a ver donde nos lleva...


## Pattern Matching

Qué es pattern matching? Podemos quedarnos con la intuición de que se trata de elegir una pieza de código para ejecutar en base a la *"forma"* de un valor. Trasladado al paradigma de objetos, esto va a implicar decidir desde afuera del mismo qué hacer basandonos en su clase y estado interno, en lugar de delegar la decisión en él. En objetos esto es una mala práctica (de hecho el antipatrón tiene un nombre y todo: *"Switch Statement"*) pero en funcional es lo más natural del mundo.

Si prestamos atención, podemos ver que el Visitor termina haciendo esto también (sólo porque lo hace a través de un ida y vuelta de mensajes en lugar de un *type check* no cambia el hecho de que el resultado final termina produciendo las mismas consecuencias que el antipatrón) con el agregado de que el código está disperso y es más difícil de seguir.

Probamos entonces una nueva solución que reemplaza el *double-dispatch* del Visitor por Pattern Matching:

[repo funcional-mutable](https://github.com/tadp-utn-frba/tadp-clases/tree/scala-microprocesador/funcional-mutable)

Aprovechamos para contar cómo trabaja Scala y sus *case-classes*.

En esta nueva implementación no sólo las instrucciones son más concisas sino que, al estar implementadas desde afuera de los objetos involucrados y no tener que preocuparse por encontrar un lugar en el algoritmo de recorrido predefinido, muchas de las operaciones terminan resolviendose mucho más fácil (ej. simplify).

Tiene sentido... Si lo que buscabamos para este caso eran las ventajas de extensión que del *Polimorfismo Paramétrico*, porqué usar un ida y vuelta de mensajes basado en *Polimorfismo ad-hoc*? Pare de sufrir!


## Transparencia Referencial

Se dice que una expresión posee *Transparencia Referencial* cuando su evaluación puede ser reemplazada por su resultado sin que dicho cambio altere el programa. Esto, que a primera vista podría parecer una característica poco relevante, encierra en cierto modo la escencia de la filosofía que mueve al paradigma funcional. La construcción principal de este paradigma es, por supuesto, *la Función*: una relación unidireccional entre un dominio y una imagen. Las funciones no cambian estados ni producen efectos, solamente conectan puntos. Programar en funcional implica pensar en el programa como un viaje entre un dominio y una imagen: "Estoy acá y quiero llegar allá. Qué pasos doy?".

Una vez que uno le toma la mano a este enfoque es sorprendentemente sencillo, no tanto porque aporte alguna construcción nueva, sino por todos los problemas que **No** tiene: No hay que preocuparse de mantener la consistencia de un estado, porque no hay estado. No hay que preocuparse por problemas de concurrencia porque todo lo que necesito para trabajar son los parámetros que recibo y no puedo modificarlos, solo analizarlos para llegar a la imagen que busco. Los algoritmos se vuelven más sencillos simplemente porque tengo menos componentes con los cuales construirlos y toda operación puede ser llamada en cualquier momento desde cualquier lugar con la certeza de que no puede cambiar al programa.

Si volvemos a mirar el código de nuestra solución actual y la comparamos con la versión anterior, podemos ver que algunas operaciones se simplificaron significativamente al reemplazar su implementación de un Visitor a una función (el simplify, por ejemplo, ya no necesita mantener un estado complejo, lo cual hace que el código sea mucho más fácil de mantener).

Sin embargo, especialmente en la operación *run*, todavía dependemos fuertemente de producir cambios de estados. Imaginemos que queremos debuguear el código del *simplify*: Podríamos empezar a ejecutarlo, poner breakpoints, evaluar la siguiente expresión o el siguiente paso para ver que retorna o volver a ejecutar un paso que ya dimos para entender porqué dio cierto resultado. Incluso podríamos "deshacer" la ejecución dropeando frames del stack de ejecución sin que eso comprometa la integridad del programa.

Podríamos hacer lo mismo con el *run*? Y... No. Cada paso de ejecución modifica el estado interno del Micro (e incluso algunos pasos podrían lanzar una excepción), con lo cual, si durante un debugue ejecutamos de nuevo el paso anterior estamos potencialmente comprometiendo el estado de ejecución, lo cual nos obliga a empezar todo de nuevo.

En el siguiente ejemplo nos paramos en el código anterior para ir gradualmente eliminando el uso de efecto colateral y mutabilidad y acercarnos de a poco a la *Transparencia Referencial*.

[repo funcional-inmutable](https://github.com/tadp-utn-frba/tadp-clases/tree/scala-microprocesador/funcional-inmutable)

Una consecuencia feliz de esta nueva aproximación es que ahora que nuestra ejecución es una función (o sea, va de un dominio a una imagen) su tipo es mucho más representativo de lo que hace:

~~~scala
  run(m: Micro, p: Program): Result // Describe muy claramente lo que pasa, porque pasa poco y nada. Solamente va de un micro y un programa a un resultado.
  run(m: Micro, p: Program): Unit   // No termina de contar para qué sirve, ni qué cambia de cuál parámetro
~~~

La clase que viene vamos a partir de esta solución y, luego de aprender algunas cosas nuevas, vamos a tratar de mejorarla un poco más utilizando *Orden Superior* y abstracciones de más alto nivel.


[1] [SourceMaking: Visitor](https://www.google.com/url?q=https://sourcemaking.com/design_patterns/visitor&sa=D&ust=1465054556955000&usg=AFQjCNF250rlMRq7KCXAtkNT9cwuhA_cxA)
