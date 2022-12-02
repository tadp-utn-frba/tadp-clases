# I/O

## Introducción

En clases anteriores vimos con el ejercicio del microcontrolador como partir de una implementación en objetos puro
y fuimos introduciendo conceptos de funcional hasta llegar a una implementación inmutable, con pattern matching
y usando mónadas para evitar lanzar exceptions.

Uno de los objetivos que buscabamos era la ausencia de efecto, pero dejamos afuera algunos tipos de efecto bastante importantes para
el desarrollo de un sistema:

- Leer/escribir de consola
- Leer/escribir un archivo
- Hacer un request a un server
- Hacer una query a una base de datos
- etc

Para poder tener estas capacidades y mantener la pureza, en haskell se utiliza la mónada I/O. Es un tipo abstracto de datos
que representa la descripción de una acción o efecto, pero no su resultado.

## ZIO

Para el ejemplo vamos a usar una implementación de la mónada I/O en scala: [ZIO](https://zio.dev/).

### Principales tipos de datos

- **ZIO[-R, +E, +A]**: es un valor inmutable que representa una acción de forma lazy.
    - R es el tipo del ambiente: representa los requerimientos que necesita el efecto. Si R es Any, quiere decir que
      el efecto no tiene requerimientos.
    - E es el tipo que va a producir si el efecto falla. Si es Nothing, significa que no puede fallar.
    - A es el tipo que va a producir si el efecto termina correctamente. Si es Unit, quiere decir que no produce nada
      útil. Si es Nothing significa que el efecto corre indefinidamente(o hasta que falle).
- **Task[A]**: alias de **ZIO[Any, Throwable, A]**. No tiene requerimientos, falla con Throwable o produce A.
- **RIO[R, A]**: alias de **ZIO[R, Throwable, A]**. Requiere R, falla con Throwable o produce A.
- **IO[E, A]**: alias de **ZIO[Any, E, A]**. No tiene requerimientos, falla con E o produce A.
- **UIO[A]**: es un alias de **ZIO[Any, Nothing, A]**. No requiere nada, no puede fallar y produce A.
- **URIO[R, A]**: alias de **ZIO[R, Nothing, A]**. Requiere un R, no puede fallar y produce A.

### Comparación con otras mónadas
ZIO es suficientemente general para poderse usar en lugar de otras mónadas.
- `Option[A]` es equivalente a `ZIO[Any, Nothing, A]` o `UIO[A]`. En realidad `ZIO.fromOption` devuelve `IO[Option[Nothing], A]`
```scala
  /**
   * Lifts an `Option` into a `ZIO` but preserves the error as an option in the
   * error channel, making it easier to compose in some scenarios.
   */
  def fromOption[A](v: => Option[A]): IO[Option[Nothing], A]
```
- `Try[A]` es equivalente a `ZIO[Any, Throwable, A]` o `Task[A]`
```scala
  /**
   * Lifts a `Try` into a `ZIO`.
   */
  def fromTry[A](value: => scala.util.Try[A]): Task[A]
```
- `Either[E, A]` es equivalente a `ZIO[Any, E, A]` o `IO[E, A]`
```scala
  /**
   * Lifts an `Either` into a `ZIO` value.
   */
  def fromEither[E, A](v: => Either[E, A]): IO[E, A]
```
- `Future[A]` requiere un `ExecutionContext`(el encargado de crear los threads) para poder crearse.
  Se puede ver como un `ZIO[ExecutionContext, Throwable, A]`.
```scala
  def fromFuture[A](make: ExecutionContext => scala.concurrent.Future[A]): Task[A]
```
  
### Composición
Al manejar siempre el mismo tipo de mónada, se gana la ventaja que todo se puede componer muy fácilmente.
Aprovechando esto, los desarrolladores de ZIO implementaron varias cosas interesantes:
- **Scheduling**: recetas para ejecutar un efecto recurrentemente. Se pueden definir formas de repetir un efecto, reintentarlo,
  con un backoff, jitter, etc.
- **Fibers**: es la manera de tener concurrencia. Se pueden ver como threads virtuales. Un feature interesante es `race`: 
  permite ejecutar dos tareas en paralelo y retornar el resultado de la primera que termine.
```scala
  /**
 * Returns an effect that races this effect with the specified effect,
 * returning the first successful `A` from the faster side. If one effect
 * succeeds, the other will be interrupted. If neither succeeds, then the
 * effect will fail with some error.
 *
 * Note that both effects are disconnected before being raced. This means that
 * interruption of the loser will always be performed in the background. This
 * is a change in behavior compared to ZIO 2.0. If this behavior is not
 * desired, you can use [[ZIO#raceWith]], which will not disconnect or
 * interrupt losers.
 */
final def race[R1 <: R, E1 >: E, A1 >: A](that: => ZIO[R1, E1, A1]): ZIO[R1, E1, A1]
```
- **Manejo de recursos**: cuando se accede a un recurso(por ejemplo: un archivo) un patrón muy común es usar un try/catch/finally
  para asegurarnos de que se cierre/libere el recurso. Para eso mismo existe `ZIO.acquireReleaseWith`:
```scala
ZIO.acquireReleaseWith(acquire = ???)(release = ???)(use = ???)
```

### Patterns/Helpers out of the box
- Conversiones de tipos comunes (los que vimos arriba, `fromTry`, `fromOption`, etc)
- Blocking: `succeedBlocking`, `blocking`...
- Retrying and Backoff: `retry`, `retryWhile`, `retryUntil`, `Schedule`...
- Console: `readLine` y `printLine` 
