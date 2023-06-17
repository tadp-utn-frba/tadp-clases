# Clase 10 - Ejercicio en Funcional

## Objetivos de la clase:

Ver un poquito de diseño en funcional. La idea es partir de soluciones imperfectas, o lejanas del grado de abstracción y declaratividad que uno querría tener en un diseño funcionaloso (con mucho pattern matching, muchas estructuras muy expuestas) y empezar en cambio a introducir funciones como objetos y objetos como funciones para ir hacia algo que aplique algo más de orden superior, que use funciones para abstraer la lógica.

## TP a utilizar:
En principio, la intención es usar el TP de Pokemon.

Se utilizó esta versión [reducida](https://docs.google.com/document/d/1aewVvR4YcU3-1fXZDKEGEGgH5i2ar-mTpugKn2uGMhg/edit#)

Podemos basarnos en la implementación de referencia que hizo [NicoS](https://github.com/bossiernesto/tp-pokemon)

Y también podemos basarnos en la implementación que hizo el grupo [wololos](https://github.com/iluetich/wololos-1c-2015-tp-tadp-pokemon)

- Comenzar planteando el caso de uso de implementar algunas actividades sencillas y sin parámetros extra: Descansar y fingir intercambio.
- Con estas dos actividades, debería surgir la idea de que quizás las actividades pueden ser funciones, pero de momento podemos dejarla de lado. Que quede colgada.
- Agregar más actividades: Nadar y levantar pesas. Vemos que tienen un parámetro. Las agregamos como case classes.
- Hacer funcionar las actividades con estructuras de datos y funciones, y mucho pattern matching. Hacer la función “realizarActividad”, quizás definida en pokemon o quizás definida afuera que use Pattern matching a full para determinar qué hacer en cada actividad.
- Implementar la “Rutina” como lista de actividades, y el “realizar rutina” quizás como un fold, o algo así. O quizás empezar con una idea recursiva y después cambiar a fold. Quizás introducir el Try.
- Preguntar si dado que cada actividad conoce lo que tiene que hacer con un pokemon, no sería conveniente encapsular lo que la actividad hace dentro de cada una. Hacer quizás un método polimórfico o algo así.
- Reflexionar un poco acerca de Polimorfismo Ad-Hoc vs Pattern Matching. Ver que no son excluyentes, y que se puede usar uno o el otro donde convenga y combinarlos para lograr un diseño supuestamente flexible. Quizás hablar de la dicotomía agregar operaciones vs agregar tipos/estructuras.
- Comentar que en Scala las funciones son objetos con un contrato en particular. Ver quizás los traits de function y cosas del estilo. Ver que para que un objeto se pueda comportar como función, la idea es definir el Apply. Definirlo en las actividades que ya existen. Cambiar el “type” de Actividad para que sea una función de Pokemon en Pokemon. Cambiar el realizar rutina para que foldee aplicando las funciones.
- Agregar alguna actividad, más complicada, como realizar ataque, más como ejercitación que otra cosa

## Segunda parte

- Arrancamos marcando que al subir un nivel no se están actualizando las características -> hacer el cambio de val a def
- Implementar las rutinas(fold y try)
- Agregamos las evoluciones. Modificar ganarExperiencia y agregar a la especie la condición evolutiva(option). Actividades usarPiedra e intercambiar
- Ataques: validación APs suficientes, puntos de experiencia ganados, efectos posteriores
