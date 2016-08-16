# Clase 4: instance_eval, class_eval, method_missing

Queremos modelar un Meta Builder, el cual nos ofrece la posibilidad de armar Builders para cualquier clase que queramos, ahorrandonos el desarrollo de un builder por cada clase de nuestro dominio o modelo de negocio. 

La resolución del Meta Builder en clase está en el siguiente [repo](https://github.com/tadp-utn-frba/tadp-utn-frba.github.io/tree/ruby-metabuilder).

## Punto 1: Sintaxis básica del Meta Builder

En el primer punto nos piden que modelemos y desarrollemos los métodos necesarios para setear una clase y sus propiedades, para luego armar un builder con esos datos. Empezamos con lo fundamental:

~~~ruby
class Metabuilder
  attr_reader :klass, :properties, :validations

  def initialize
    @properties = []
  end

  def set_class(klass)
    @klass = klass
  end

  def set_properties(*args)
    @properties += args
  end


  def build
    Builder.new @klass, @properties, @validations
  end

end

class Builder
  attr_reader :properties, :validations

  def initialize(klass, properties, validations)
    @klass = klass
    @properties= {}
  end

  def set_property(sym, value)
    self.properties[sym] = value
  end

  def build
    instancia = @klass.new
    self.properties.each do |property, value|
      instancia.send "#{property}=".to_sym, value
    end
    instancia
  end
end
~~~

Vemos que es necesario definir las responsabilidades del Meta Builder como del Builder; el primero se encarga de definir la clase a instanciar, y sus propiedades sobre las cuales podemos trabajar en el Builder; el segundo se encarga de proveernos una interfaz para modificar los valores en dichas variables, y poder brindarnos una instancia con las propiedades ya seteadas.

### Method Missing

Cuando le enviamos un mensaje a un objeto, se ejecuta el primero que se encuentra durante el method lookup. Si no encontrase ninguno, arroja una excepción del tipo NoMethodError, salvo que ese objeto tenga definido un método para el mensaje ‘method_missing’. 
Este mensaje recibe por parámetro el nombre del mensaje que no se encontró, junto con los argumentos con los cuales fue invocado. Esto nos permite manejar esta clase de situaciones en runtime, siendo una herramienta muy poderosa para hacer, entre otras cosas, que un objeto entienda cualquier mensaje.

## Punto 2: Sintaxis específica

En el 2do punto vemos que el test nos dicta la interfaz que debemos cumplir, como extensión al modelo anterior (es decir, que debe seguir soportándolo el uso del set_property)
Como es observable, la clase Builder no entiende los mensajes ‘raza=’, ‘edad=’. Y es correcto, ya que la idea del Meta Builder debe ser agnóstico del dominio o modelo de negocio que manejemos. ¿Cómo solucionamos este problema?:

Utilizando el method_missing. Nosotros querríamos que, cuando se envíe el mensaje ‘raza=’ el Builder responda de manera tal que termine asignando el valor a dicha propiedad. Esto lo resolvemos así:

~~~ruby
class Builder

def method_missing(symbol, *args)
    property_symbol = symbol.to_s[0..-2].to_sym
    super unless self.properties.has_key? property_symbol
    self.set_property property_symbol, args[0]
 end

end
~~~

Es necesario modificar el nombre del método quitàndole el ‘=’ para poder buscarlo en el mapa que contiene todas las propiedades. Luego, verificamos que exista como propiedad, sino hacemos que continùe el flujo de ejecución del method_missing en toda la jerarquía, para mantener la consistencia de nuestro programa (sino podría pasar que tengamos la posibilidad de setear a un builder de perros que tenga una espada, por ejemplo). Finalmente, seteamos dicha propiedad con el argumento que, en este caso, es el primero. 

Warning:

Esta posibilidad que nos da Ruby de re-definir el method_missing nos da un gran abanico de posibilidades a la hora de que un objeto entienda ciertos mensajes. Es decir, podríamos hacer que una Persona responda al mensaje ‘ladrar’, por ejemplo. Pero eso no significa que las Personas tengan definido un método ‘ladrar’. Si preguntamos a un builder
de perros si entiende el mensaje ‘raza=’, nos dirá que no. Y esto termina por hacer que nuestro programa no sea consistente, dado que si le enviamos ese mensaje, el builder responde seteando un valor a dicha propiedad. 
Para mantener la coherencia de nuestro framework, podemos redefinir el mensaje respond_to_missing? de manera tal que nos indique si responde o no a un mensaje que no está explícitamente definido. Este mensaje es invocado por el respond_to? si no encuentra el selector pasado por parámetro en los métodos definidos por el objeto receptor.

~~~ruby
class Builder
    def respond_to_missing?(symbol, include_all) 
        property_symbol = symbol.to_s[0..-2].to_sym
        self.properties.has_key? property_symbol 
    end
end
~~~

Aplicando esta definición, podemos hacer esta consulta:

~~~ruby
metabuilder = Metabuilder.new
metabuilder.set_class(Perro)
metabuilder.set_properties(:raza, :edad)

builder_de_perros = metabuilder.build
builder_de_perros.respond_to? :raza=
# true
~~~

En conclusión, se suele decir que siempre que se redefina el method_missing es necesario definir el respond_to_missing?.

## Punto 3: Validaciones

Ahora se requiere que, antes de poder buildear una instancia, se corran ciertas validaciones que nos aseguren que dicha instancia es válida. Para ello, debemos poder setearle al metabuilder una serie de validaciones. Definimos el mensaje validate:

~~~ruby
class Metabuilder
    def validate(&block)
        @validations << block
    end
    
    def build
        Builder.new @klass, @properties, @validations
    end
end
~~~

Observación: como vamos a recibir un bloque (que en Ruby no son objetos), y sólo podemos guardar objetos en las colecciones, lo necesitamos transformar en un Proc. Ruby nos da una forma sintáctica bastante simple para que, al recibir un bloque, se transforme implícitamente a un Proc, esto es, utilizando el ampersand (&).

Ahora necesitamos definir que toda instancia de Builder ejecute dichas validaciones antes de retornar la instancia...
Lo que hay que tener en cuenta es que, dentro de las validaciones, se invocan mensajes que no entiende el Builder, porque son getters (:raza, :edad, :duenio). Estos mensajes sólo los entienden las instancias de Perro, o bien, de la target class que tenga el Builder. Pero cambiar el contexto de un bloque no es trivial… sin embargo, Ruby nos da una herramienta para hacer un work-around.

Instance_eval

Este mensaje recibe por parámetro un bloque, y lo ejecuta en el contexto de instancia del objeto receptor de dicho mensaje. Es decir, que podemos hacer algo así:

~~~ruby
class Builder
    def build 
        instancia = @klass.new 
        self.properties.each do |property, value| 
        instancia.send "#{property}=".to_sym, value 
      end

raise ValidationError unless @validations.all? do |validation|
instancia.instance_eval &validation 
end 
instancia
	end
end
~~~

De esta manera, las validaciones se ejecutan en el contexto de instancia de la instancia de la clase que seteamos. (es decir, de la instancia de Perro).

## Punto 4: Buildear instancias sobre una clase no existente

Como último requerimiento de la clase, nos piden extender la interfaz de manera tal que un meta builder puede crear un builder para una clase que no existe. Es decir, que tendría que crear la clase y luego pasársela al builder cuando se le envíe el mensaje ‘build’ al meta builder.
También debe ser posible recibir un bloque en el que definamos métodos de instancia para la clase en cuestión.
Definimos un mensaje ‘create_class’ que recibe el símbolo, representante de la clase, y un bloque que define su estado.

~~~ruby
class Metabuilder
def create_class(sym, &block)
@klass = Class.new
Object.const_set sym, @klass 
@klass.instance_eval &block 
end
end
~~~

#### ¿Qué es Object.const_set sym, @klass?

- Object.const_set: recibe un simbolo y un objeto, y crea una constante con el simbolo como nombre y el objeto como referencia.
- sym: en este caso, el nombre de la clase (:Perro, :Gato, etc.)
- @klass: la instancia de Class que representa a nuestra clase.

En esta sentencia estamos creando una constante en el contexto global de manera tal que pueda ser invocada desde cualquier parte de nuestro programa. Si no ejecutamos esto, perderíamos la referencia global a dicha clase.

Similar al problema anterior, nosotros querríamos que el bloque que define el estado de la clase se ejecute en el contexto de instancia de la clase (es decir, como instancia de Class), ya que si lo llamáramos con ‘call’ dentro del método ‘create_class’ se ejecutaría en el contexto de instancia de un meta builder.
