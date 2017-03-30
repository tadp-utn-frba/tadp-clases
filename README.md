# Clase 3 TADP 2C2016

## METAPROGRAMACION
Es el proceso o la práctica por la cual escribimos programas que generan, manipulan o utilizan otros programas.

###### Ejemplos
* Un compilador se puede pensar como un programa que genera otro programa.
* Un formateador de código es un programa que manipula otro programa.
* Una herramienta como javadoc utiliza nuestro programa para generar su documentación.

###### Para qué se usa la metaprogramación?
En general la metaprogramación se utiliza más fuertemente en el desarrollo de frameworks y herramientas.

Dado que los frameworks van a resolver cierta problemática de las aplicaciones, no van a estar diseñados para ninguna en particular. Es decir, la idea de framework es que se va a poder aplicar y utilizar en diferentes dominios desconocidos para el creador del framework.

Ejemplos:

- ORM's (como hibernate): Que van a encargarse de persistir las instancias de nuestras clases sin siquiera conocerlas de antemano.
- Frameworks de UI: Que deberán saber mostras / bindear cualquier objeto.
- Frameworks de Testing (como JUnit): suelen usar metaprogramación para analizar la clase de Test y encontrar los tests que se deben correr.
- Documentadores de Código: Son herramientas que leen el código fuente y genera documentación (por ejemplo: JavaDoc).
- Code Coverage: Herramientas que miden cuánto de nuestro código es realmente ejecutado al correr los tests, y cuales lineas no.
- Analizadores de código: Que evalúan nuestro código y genera métricas o miden violaciones a reglas definidas. Como el estilo de código, complejidad ciclomática, etc.

## Reflection
Es un caso particular de metaprogramación, donde "metaprogramamos" en el mismo lenguaje en que están escritos (o vamos a escribir) los programas. Es decir, todo desde el mismo lenguaje.

### Tipos de reflection
Para esto, generalmente, es necesario contar con facilidades o herramientas específicas, digamos "soporte" del lenguaje. Entonces reflection, además, abarca los siguientes items que vamos a mencionar en esta lista:

- **Introspection**: Se refiere a la capacidad de un sistema, de analizarse a sí mismo. Algo así como la introspección humana, pero en términos de programa. Para eso, el lenguaje debe proveer ciertas herramientas, que le permitan al mismo programa, "ver" o "reflejar" cada uno de sus componentes.
- **Self-Modification**: Es la capacidad de un programa de modificarse a sí mismo. Nuevamente esto requiere cierto soporte del lenguaje. Y las limitaciones van a depender de este soporte.
- **Intercession**: Es la capacidad de modificar las características del lenguaje desde el mismo. Por ejemplo: agregarle orientacin a objetos a Lisp (CLOS).

### Modelos y metamodelos
Así como todo programa construye un modelo para describir su dominio, los lenguajes pueden hacer lo mismo para describir sus abstracciones. El domino de un metaprograma son los programas.

El programa describe las características de los elementos del dominio utilizando clases, métodos, atributos entre otros. Entonces, el modelo puede contener por ejemplo una clase Guerrero, que modela a los guerreros en el domino.

Un metaprograma usará el metamodelo que describe al programa base. Así como en el dominio hay guerreros, los elementos del "metadominio" serán las construcciones del lenguaje. Por ejemplo: clases, atributos, métodos.

# Practica
Vamos a usar el código de la clase anterior como ejemplo: https://github.com/tadp-utn-frba/tadp-clases/tree/ruby-age

Podemos comenzar con un poco de introspection y preguntarle a un objeto desde su clase hasta que metodos tiene.

```ruby
require_relative 'age'
atila = Guerrero.new
atila.class  #=> Guerrero
atila.class.superclass  #=> Object

atila.methods  #=> [:sufri_danio, :descansar, :energia, :energia=,...]
Guerrero.instance_methods #=> Idem a atila.methods
Guerrero.instance_methods(false) #=> [:descansar_atacante, :descansar_defensor,...] (solo los de la clase guerrero)
```

Tambien podemos empezar a interactuar con los objetos de otra manera, como mandarle mensajes de otra manera.

```ruby
atila.send(:potencial_ofensivo)  #=> 20
atila.send(:descansar)  #=> 110

# con send no existen los metodos privados, la seguridad es una sensacion
class A
    private
    def metodo_privado
        'cosa privada, no te metas'
    end    
end
objeto = A.new
objeto.metodo_privado #=> NoMethodError: private method `metodo_privado' called for #<A:direccion en memoria del objeto>
objeto.send(:metodo_privado)  #=> "cosa privada, no te metas"
```
## Method / Unbound Method
Tambien podemos obtener un metodo e invocarlo

```ruby
metodo = atila.method(:potencial_ofensivo)  #=> #<Method: Guerrero(Atacante)#potencial_ofensivo>
metodo.call  #=> 20
```

Algo interesante que podemos hacer es pedirle un metodo de instancia a una clase. A este metodo se lo llama Unbound Method, ya que no esta asociado a ninguna instancia de esa clase. Podemos asociarlo a un objeto siempre y cuando este dentro de la jerarquia de clases.

```ruby
class Padre
    def correr
        'correr como padre'
    end
end

class Hijo < Padre
    def correr
        'correr como hijo'
    end
end
metodo = Padre.instance_method(:correr)  #=>#<UnboundMethod: Padre#correr>
metodo.bind(Hijo.new).call  #=> 'correr como padre'
```

Como vemos los Unbound Methods se escapan al metodo lookup.
Tambien podemos pregunarle cosas a los metodos.

```ruby
metodo = atila.method(:atacar) #=> #<Method: Guerrero(Atacante)#atacar>
metodo.arity  #=> 1
metodo.parameters #=> [[:req, :un_defensor]]
metodo.owner  #=> Atacante (donde esta definido)
```

Asi como ya estuvimos jugando con las clases, objetos y metodos, tambien podemos jugar con las variables.

```ruby
atila.instance_variables #=> [:@potencial_ofensivo, :@energia, :@potencial_defensivo]
atila.instance_variable_get(:@energia)  #=> 100
atila.instance_variable_set(:@energia, 50) #=> 50
atila.instance_variable_get(:@energia)  #=> 50
atila #=> pretty print muestra el estado interno
```

### Self-Modification
#### Open classes

Nos permite definir métodos y atributos en una clase ya existente.
Es una forma de self modification con azucar sintáctica para no tener que hacerlo mediante mensajes.

Los métodos que se definan con el mismo nombre que otro ya existente, serán reemplazados (es destructivo).

Para Ruby, las firmas de los métodos están definidas solo por el nombre. Un método con el mismo nombre y diferente cantidad de parametros definen el mísmo método.

```ruby
class String
  def importante
    self + '!'
  end
end
'aprobe'.importante  #=> "aprobe!"

#Cambiar métodos
class Fixnum
  def +(x)
    123
  end
end
2+2  #=> 123
```

Otra manera de abrir las clases y definir metodos es usando en la clase el ´define_method´ pero este es privado y como ya vimos podemos pasarlo por arriba invocando el send.
 
```ruby
Guerrero.send(:define_method, :saluda) {
  'Hola'
}
Guerrero.new.saluda  #=> "Hola"
```

Aca podemos hacer referencia a dos practicas de programacion: **Duck Typing** y **Monkey Patching**.

#### Duck Typing
Debido a que ruby es un lenguaje dinamicamente tipado, hacemos referencia a un tipo de dato por el comportamiento que tiene.
>...if it walks like a duck and talks like a duck, it’s a duck, right? 

Si tenemos un objeto que cuando hace ruido hace "cuak" y camina como un pato, probablemente lo sea, y deberia poder continuar usando este objeto como si fuera uno.

#### Monkey Patching
>...if it walks like a monkey and talks like a monkey, it’s a monkey, right? So if this monkey is not giving you the noise that you want, you’ve got to just punch that monkey until it returns what you expect.

Hace referencia a la posibilidad de practicamente modificar un tipo a gusto y piacere para que responda a nuestras necesidades y realizar otro tipo de operaciones como si fuera otro.

### Singleton Class
Tambien podemos empezar a hacer algunas cosas mas locas, como agregarle comportamiento a un unico objeto

```ruby
atila.define_singleton_method(:saluda) {
  'Hola soy Atila'
}
atila.saluda  #=> "Hola soy Atila"
Guerrero.new.saludo # NoMethodError
```

## METAMODELO
Empecemos a descubrir el modelo de clases.

Vamos a jugar un poco más con el metamodelo, ya sabemos que existe el mensaje ´class´ que lo entienden todos los objetos, si queremos saber la superclase de una clase tenemos el mensaje ´superclass´. Podríamos pensar en base a eso quiénes le proveen comportamiento a cada uno de nuestros objetos.

```ruby
zorro = Espadachin.new(Espada.new(123))
```

Tenemos al zorro que es instancia de Espadachin, que hereda de Guerrero. Si le pedimos los métodos al zorro vemos que incluye a los ´instance_methods´ de Espadachin y de Guerrero, así como todos los que se definen para las instancias de Object.

### Autoclases / Eigen Class
Pero nosotros no le agregamos comportamiento sólo a las instancias, también teníamos un par de métodos de clase que habíamos definido para Peloton, como por ejemplo cobarde.

```ruby
Peloton.cobarde([])
```

Quién provee ese comportamiento? La clase de Peloton es Class, y este comportamiento no se agregó para todas las clases así que no puede estar definido dentro de Class.

```ruby
Peloton.methods.include? :cobarde  #=> true
Peloton.class.instance_methods.include? :new  #=> true
Peloton.class.instance_methods.include? :cobarde  #=> false
```

Esto sólo lo entiende la clase Peloton, o sea que está definido para un sólo objeto. El objeto que le provee el comportamiento a un sólo objeto es la autoclase. En Ruby podemos obtener la autoclase de un objeto mandándole ´singleton_class´.

```ruby
Peloton.singleton_class.instance_methods(false)  #=> [:cobarde, :descansador]
```

Todos los objetos tienen una singleton class, con lo cual podemos definirle comportamiento a atila y que sea el único guerrero con ese comportamiento.

Incluyanmos un mixin a una instancia, utilizando include en la singleton class o extend en la intancia:

```ruby
module W
  def m
    123
  end
end

a = Guerrero.new
a.singleton_class.include W
a.m  #=> 123
b = Guerrero.new
b.extend W
b.m  #=>123
```

Agreguemos un test en el que atila cuando se lastima descansa y se come un pollo, incorporando ´comerse_un_pollo´:

```ruby
atila = Guerrero.new
atila.singleton_class.send(:define_method, :comerse_un_pollo, proc { @energia += 20 })
atila.energia
atila.comerse_un_pollo
atila.energia
Guerrero.new.comerse_un_pollo  # NoMethodError
```

También vemos que se puede tener properties para un único objeto:

```ruby
atila.singleton_class.send(:attr_accessor, :edad)
atila.edad = 5
atila.edad  #=> 5
Guerrero.new.edad  #=> NoMethodError
```

Vemos que no aparecen los mixins que tiene la clase Guerrero si vamos preguntando las super clases. El mensaje ´superclass´ no mustra los mixins (porque no son clases), para poder ver la linearización tenemos que pedir quienes proveen comportamiento con el mensaje ancestors.

```ruby
Guerrero.ancestors  #=> [Guerrero, Defensor, Atacante, Object, PP::ObjectMixin, Kernel, BasicObject]
```

#### Nota
Las ultimas veriones de ruby incluyen a la singleton class de Guerrero, las anteriores a 2.1.0 NO incluyen a las singleton classes:

```ruby
Guerrero.new.singleton_class.ancestors

[#<Class:#<Guerrero:0x00000001d6c8c8>>,
Guerrero,
Defensor,
Atacante,
Object,
PP::ObjectMixin,
Kernel,
BasicObject]
```

Dibujar los ancestors de Guerrero en el pizarron.
Dibujar la singleton class de atila.
Agregamos un método de clase a Guerrero:

```ruby
class Guerrero
  def self.gritar
    'haaaa'
  end
end

atila.gritar  #=> NoMethodError
Guerrero.gritar  #=> haaaa
```

Veamos en el diagrama, la singleton class de Guerrero (donde definimos ´gritar´)

```ruby
Espadachin.gritar  #=>haaaa
```

![](https://raw.githubusercontent.com/tadp-utn-frba/tadp-clases/ruby-metaprogramming/Metamodelo_de_Ruby.jpg)
