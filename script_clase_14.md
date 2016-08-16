#Implicits y Macros

## Implicits

- Extender codigo de otro (que no puedo modificar)
- DSLs (mostrar specs2)


### Implicit convertion

- No es recomendable hacer una conversión implicita que pierde información

#### Conversión a tipo esperado

~~~scala 
case class Persona(nombre: String, padre: Option[Persona] = None, madre: Option[Persona] = None) {
 def procrearCon(persona: Persona) = Persona(nombre + " jr.", Some(this), Some(persona))
}
def saluda(persona: Persona) = s"Hola ${persona.nombre}"
//...
saluda(Persona("Adan")) // "Hola Adan"
//...
implicit def strToPersona(str: String) = Persona(str)

message(User("Pepe", "Pompin")) //> res0: String = Hola Pepe Pompin

implicit def stringAsUser(str: String): User = {
  val a = str.split(' ')
  User(a(0), a(1))
}

val u: User = "Pepe Pompin" //> u : User = User(Pepe,Pompin)
message("Pepe Pompin")      //> res1: String = Hola Pepe Pompin
~~~

#### Conversión del receptor del mensaje / Extension methods 

~~~scala
implicit def userConMessage(user: User) = new {
  def message(end: String) = s"Hola ${user.firstName} ${user.lastName} $end"
}
  
User("Pepe", "Pompin").message("!")

implicit val userConMessagePosta = (user: User) => new {
  def messagePosta(end: String) = s"Hola ${user.firstName} ${user.lastName} $end"

}

User("Pepe", "Pompin").messagePosta("!")

implicit class UserConMessage(user: User) {
  def messageNuevo(end: String) = s"Hola ${user.firstName} ${user.lastName} $end"
}

User("Pepe", "Pompin").messageNuevo("!")
object User {
  implicit class UserOpt(user: User) {
    def otro(end: String) = s"Hola ${user.firstName} ${user.lastName} $end"
  }
  
  implicit class PartialUser(firstName: String) {
    def &(lastName: String) = User(firstName, lastName)
  }
}

User("Pepe", "Pompin").otro("!")
  
import implicits.User.PartialUser
val x: User = "Pepe" & "Pompin" //> x  :User = User(Pepe,Pompin)
message("Pepe" & "Pompin") //> res6: String = Hola Pepe Pompin
~~~

- Ruby MonkeyPatch: Peligroso => El cambio es global, no puedo usar dos implementaciones distintas, puedo agregar fields
- C# Extension Methods: dependen del scope (puedo usar multiples implementaciones sin conflicto), no cambian al tipo original (tienen dispatch estático)

#### Implicit parameter 

- Los parámetros implicitos también son variables implicitas declaradas dentro del scope de la función

~~~scala
trait Config {
  def end: String
}

def message(user: User)(implicit config: Config) = s"Hola ${user.firstName} ${user.lastName} ${config.end}"

implicit object duda extends Config {
  def end = "?"
}
implicit val admiracion = new Config {
  def end = "!"
}

import implicits.Parameters.duda
message(User("Pepe", "Pompin")) //> res0: String = Hola Pepe Pompin ?

// ...

import implicits.Parameters.admiracion
message(User("Pepe", "Pompin"))  //> res1: String = Hola Pepe Pompin !

val config = new Config {
 def end = ":)"
}
message(User("Pepe", "Pompin"))(config) //> res2: String = Hola Pepe Pompin :)

// ...

def chainDeImplicits(user: User)(implicit config: Config) = message(user)

implicit val feliz = config
chainDeImplicits(User("Pepe", "Pompin")) //> res3: String = Hola Pepe Pompin :)
~~~

####Implicits looup order

-(http://docs.scala-lang.org/tutorials/FAQ/finding-implicits.html)[http://docs.scala-lang.org/tutorials/FAQ/finding-implicits.html]
- (
http://jsuereth.com/scala/2011/02/18/2011-implicits-without-tax.html)[
http://jsuereth.com/scala/2011/02/18/2011-implicits-without-tax.html]

1. First look in current scope
..* Implicits defined in current scope (including package objects)
..* Explicit imports
..* wildcard imports
2. Now look at associated types in
..* Companion objects of a type (including super classes companion objects)
..* Implicit scope of an argument’s type
..* Implicit scope of type arguments
..* Outer objects for nested types
..* Other dimensions

#### View Bounds

No contar sobre view bounds porque están siendo (deprecados)[https://issues.scala-lang.org/browse/SI-7629].

#### Context bound

Implicit parameter de un tipo parametrizado.
- implicitly

###Type classes (polimorfismo ad-hoc)

- No todos los hijos de una jerarquía tienen que implementar ese comportamiento
- Pueden haber varias implementaciones viviendo juntas sin conflictos
- Podemos hacer que un tipo implemente una typeclass sin tener que modificar su codigo (puedo generar typeclasses para codigo de terceros)
- Pueden ser constructoras de instancias (para poder usar la implementación de la typeclass no necesito una instancia del tipo) (Monoid zero)



- Ordered vs Ordering + operadores implicitos
- numeric (listas.sum) + operadores implicitos
- ejercicio

### Formas de compartir codigo

- herencia / mixins una sola implementacion polimorfica por tipo
- composicion / delegación (strategy) / wrapper
- Extension methods / implicit conversions no son polimorficos
- comparar con monkey patch de ruby

