## Introducción a chequeo estático de tipos

##### Primero vamos a comparar con lo que hacíamos anteriormente en Ruby

~~~ruby
class Persona
  :attr_accessor :name
  
  def initialize(name)
    self.name = name
  end
end

class Saludador
  def saludar(a)
    "asd" + a.nombre
  end
end

Saludador.new().saludar(Persona.new("asd"))
~~~

Esto rompe en ejecución cuando intento mandar este mensaje, porque persona no entiende :nombre, sino que entiende :name.

Tendríamos que agregar algún tipo de validación para que esto no pase, o sea, un sistema de tipos.

Además, para poder ser usado con saludador, lo que le pasamos al método saludar tiene que ser una Persona
o alguien que entienda :nombre.

Cuando hablamos de tipos, hablamos de **un conjunto de elementos o valores** asociado a **un conjunto de operaciones**.

Si el saludador no puede interactuar con las operaciones que yo espero que interactúe, entonces tenemos un **error de tipos**.

En objetos, el tipo de uno de ellos (valores) está definido por el conjunto de métodos que entiende. Si hay dos elementos que entienden
un mismo mensaje, entonces comparten un tipo, pero no quiere decir que sean del mismo.

#### Tipado

- Operacional: aplica a todo lo que uno hace, sea computacional o no. Trato de llevar a cabo una operacion sobre un 
elemento. 
- Representacional: solo aplica a lo computacional, llevar un tipo a un formato computable. Significa asumir una representación que no es correcta para ese tipo.
- Estructural: Ej, si haskell permitiera hacer algo como: f [] = ...   f 1 = ... 

No pasa por qué tecnología tengo para extender el tipo o no, ni tampoco por si va a saltar cuando lo ejecute o antes, porque eso es parte del chequeo.

#### Chequeo

- Dinámico: Lo hace cuando yo le mando el mensaje al objeto. Ej: Ruby.
- Estático: Decide si un programa es válido o no, analiza estáticamente el programa antes de que lo ejecutemos.
Esto permite detectar más errores que en el chequeo dinámico, por ejemplo, con el problema que tuvimos al principio
de la clase, donde persona no entiende :nombre. En este caso, no vamos a poder llegar a la instancia en la que ejecutemos
y rompa. El análisis es sobre la **declaración**. Ej: Haskell.

###### Ahora, en Scala:

~~~scala
object Saludador extends App {
  case class Persona(name: String)
  
  class Saludador {
    def saludar(alguien: Persona) =
      "hola" + alguien.nombre
  }
  
  new Saludador().saludar(new Persona("asdds"))
}
~~~

Además, si la firma de saludar fuese diferente y recibiera algo de tipo Any, por más de que en todo el código nunca se
llame al metodo saludar con algo que no entienda el método nombre, rompe igual porque en el chequeo estático se fija
en el código, y cualquier cosa que le pase no entiende nombre. 

!! Puedo tener polimorfismo sólo entre dos cosas que yo establecí **explícitamente** que comparten un tipo !!

### Notación
- Explícita: Se escribe el tipo. Puede existir un mecanismo de inferencia de tipos que facilite el trabajo de escribir los tipos explícitamente.
La inferencia sale de la notación explícita y el tipado estático y permite implicitar algo que de otra forma tendrías que haber explicitado. 
Haskell tiene tipado explícito **inferido**. Scala tiene inferencia, pero no puede inferir todo.
- Implícita

#### Conformación
- Estructural (duck-typing, pattern matching): Un tipo se referencia por su forma. Ej: `f (_,_,a) = a`
- Nominal: Un tipo se referencia por su nombre. Ej: String, Int

### Errores
- Errores de tipo: siempre está en el programa, esté en ejecución o no, no importa cómo hago el chequeo.
- Errores detectados

**equivalente a**

- Programas que puedo hacer con un tipado estático: Un chequeo estático de tipos me permite detectar los programas válidos en compilación. El compilador es el que determina si funciona o no.
- Programas que puedo hacer sólo con tipado dinámico

###### Errores que creemos que deberían ser de tipo pero en teoría no lo son

~~~scala
val x = 5
val y = 0

x/y // Los numeros entienden la división, por eso no sería un error de tipos en realidad

val list = List()
list.head // Las listas también entienden head, el problema es que está vacía
~~~

Acá aparece la **operación parcial**, que encapsula a un error de tipos, cuando puramente no lo son.

## Desarrollo del ejercicio

Si a un parámetro le pongo val o var, lo expone como si fuera público.
Puedo recrear un constructor haciendo algo del estilo class A(variableA = valor) pero en realidad no es un constructor en sí mismo.

Necesitamos que un parámetro esté expuesto, si tenemos que accederlo después, por ejemplo

~~~scala
class Guerrero(potencialOfensivo = 10)

def atacarA (unGuerrero : Guerrero) = {
 .. .... unGuerrero.potencialOfensivo // si no estaba expuesto en la clase, no podía hacerlo.
}
~~~

En este caso, era trivial poner ese **=**, de lo contrario me va a devolver **Unit** (void).

No es necesario poner las llaves si tengo una sola expresión adentro.

En cambio, si yo quiero hacer algo del estilo

~~~scala
atila.atacarA(unaMuralla)
~~~

No funciona, aunque podría ser perfectamente válido, porque entiende los mismos mensajes. Pero yo explicité el tipo de unGuerrero, y debe ser Guerrero.

En cambio, si en vez de definir el tipo de forma **nominal** como lo teníamos, y lo hacemos **estructural**:

~~~scala
type Atacable = {
  def potencialDefensivo : Int  // metodo abstracto
  def perderEnergia(a:Int) : Unit // metodo abstracto
  }
  
def atacarA(unAtacable : Atacable) = {
  ....
  }
~~~

Lo que hicimos en este caso, fue decir que atacarA recibe a cualquier cosa que entienda esos dos métodos con esa firma.
Ahora, puede aceptar tanto Muralla como Guerrero.

Cambiemos la forma de hacer esto:

~~~scala
abstract class Defensor = {
  def potencialDefensivo : Int  // metodo abstracto
  def perderEnergia(a:Int) : Unit // metodo abstracto
  var energia = 100
  }

class Muralla(altura: Int) extends Defensor {
  def potencialDefensivo = altura  * 10
  // val energia = 1000 no puedo hacer esto porque me falta un **override**
  // override val energia = 1000 tampoco, porque energia tiene solo un getter y no un setter,
  //y en la clase abstracta tenia un var, que tiene getter y setter.
  // var energia = 1000 tampoco, porque estaría pisando la variable energia con otra variable energia.
  energia = 1000
  }
~~~

Al extender una clase, tengo que pasarle tambien los parametros a la clase de la cual está extendiendo.
La linearización va de derecha a izquierda, teniendo menos prioridad la superclase siempre porque esa clase podria tener superclases, muchos mixins.

~~~scala
extends Klass with Mixin1 with Mixin2 with Mixin3
~~~

En este caso, tiene más prioridad el Mixin3 y menor prioridad su superclase Klass.

~~~scala
  def leerDeLaBase(q: String): Any = {
  //....
  new Guerrero(3,13)
  
  }
  // no podemos hacer  val atila = leerDeLaBase("asdasd"), sino:
  val atila = leerDeLaBase("asdasd").asInstanceOf[Guerrero]
~~~

Aunque lo que estamos devolviendo sabemos que es un Guerrero, como no le explicitamos que era uno, sino que era un Any
esto no compila. Hay que castearlo. Ahora puedo tratar al guerrero como tal. En cambio, si:

~~~scala
  def leerDeLaBase(q: String): Any = {
  //....
  new Guerrero(3,13)
  "asdasd"
  }
  // lo de abajo rompe
  val atila = leerDeLaBase("asdasd").asInstanceOf[Guerrero]
~~~

Y está bien que rompa. Porque no hay ningún lugar en el cual compartan un tipo o se pueda convertir un String a un Guerrero.
