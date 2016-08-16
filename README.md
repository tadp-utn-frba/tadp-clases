# Clase 6: Ejercicio Integrador N° 2

En esta clase veremos otro ejercicio integrador con el mismo fin que la clase pasada. Para ello realizaremos el ejercicio de Multimethods. 

## Que es multimethod?

Es un sistema que permite que una serie de métodos sean polimórficos en al menos uno o más de sus argumentos. Esta idea se puede ver en CLOS (Common Lisp Object System) y algunos lenguajes como Dylan. CLOS extiende a Common Lisp para agregar un sistema de objectos [http://c2.com/cgi/wiki?TheArtOfTheMetaObjectProtocol](http://c2.com/cgi/wiki?TheArtOfTheMetaObjectProtocol) extendiendo el lenguaje y para dar soporte a objetos. 

## Una explicación simple de Multimethods

En Ruby como en Python, ambos tienen sistemas de single dispatch, en el que la firma es esencialmente el nombre del método, sin importar en temas como la aridad, por lo que dos métodos como 

~~~ruby
def saraza(a)
….
end
~~~

y 

~~~ruby
def saraza(a, b, c)
….
end
~~~

no serán dos métodos sino que dependiendo de cómo estén declarados en el caso de Ruby, la segunda declaración pisa a la primera, en el caso de Python, en general sucede lo mismo en un intérprete o directamente nos lanzará un error en el código. Más allá de eso, nosotros queremos que saraza(a) y saraza(a, b) puedan coexistir y el método a ejecutar dependa de cuáles sean los parámetros recibidos. Por lo que deberemos chequear esto en tiempo de ejecución y dirigir el flujo al método con la firma adecuada. Esto se relaciona un poco con el concepto de polimorfismo del receptor, por ej. si tenemos algo como:

~~~ruby
a.saraza(unParam)
~~~

a ejecutará un método u otro dependiendo de la clase a la que pertenece, queremos abrir la puerta a que también se decida en base al tipo de unParam. Vamos a hablar más sobre firmas de métodos y la búsqueda de la definición para un mensaje a partir de la misma en la segunda parte de la materia, cuando trabajemos con un lenguaje con tipado estático. De momento quédense con la idea de que lo que queremos implementar va a resolver qué definición usar en tiempo de ejecución, a partir del tipo de todos los objetos involucrados en el envío del mensaje, no sólo el receptor.


## En donde entra la metaprogramación?

En este caso extenderemos a Ruby agregándole multimethods, de esta manera estamos un poco más del lado de intercession que es la capacidad de extender el lenguaje en el que estamos para agregarle más features interesantes (o locos?). Además como otros ejercicios usaremos reflection y self modification para llegar a resolver este problema.

Sobre el ejercicio

El enunciado del ejercicio esta [acá](https://www.google.com/url?q=http://drive.google.com/open?id%3D1_yCtJQdQbhaeWny5ByMJKNJKpCmEdTELoTEoCZG4_4Q&sa=D&ust=1471097603702000&usg=AFQjCNHbRgVqvmyQL0fi9u3_cIJPY15dXQ)

El código al que llegamos al final de la clase esta en este [repo](https://www.google.com/url?q=https://github.com/uqbar-paco/tadp-2015c2-clase6-multimethods/tree/2016-1c&sa=D&ust=1471097603703000&usg=AFQjCNFun2wmaE3O82_8PFpqQip0UG49bA)

En el primer punto implementamos algo del estilo

~~~ruby
helloBlock = PartialBlock.new([String]) do |who|
  "Hello #{who}"
end

helloBlock.matches("a") #true
helloBlock.matches(1) #false
helloBlock.matches("a", "b") #false
~~~

Acá lo que hicimos fue algo bastante simple que es crear la abstracción del PartialBlock que contenga los tipos de los parámetros esperados y el bloque que se espera poder ejecutar, y después le definimos el método matches devolviendo true o false dependiendo de si los tipos esperados coinciden con los de los argumentos que le pasamos al método. Este método debía definirse con varargs para poder recibir múltiples argumentos como se indicaba en el ejemplo de uso.

~~~ruby
class PartialBlock
  attr_accessor :block, :types

  def initialize types, &block
    self.types = types
    self.block = block
  end

  def matches(*values)
    unless values.length == types.length
      return false
    end

    return true
  end

end
~~~

Además para poder evaluarlos como se pide a continuación usando call le agregamos:

~~~ruby
class PartialBlock

  def call(*args)
    raise ArgumentError unless self.matches(*args)
    self.block.call(*args)
  end

end
~~~

2. El segundo punto es el de poder crear multimethods de la mano de partial_def, que debemos poder utilizarlo en el contexto de una clase, de modo que luego las instancias de esa clase puedan responder al mensaje correspondiente. 

~~~ruby
class A
  partial_def :concat, [String, String] do |s1,s2|
    s1 + s2
  end

  partial_def :concat, [String, Integer] do |s1,n|
    s1 * n
  end

  partial_def :concat, [Array] do |a|
    a.join
  end
end

A.new.concat('hello', ' world') # devuelve 'helloworld'
A.new.concat('hello', 3) # devuelve 'hellohellohello'
A.new.concat(['hello', ' world', '!']) # devuelve 'hello world!'
A.new.concat('hello', 'world', '!') # Lanza una excepción!
~~~

Ahora debemos definir el comportamiento del partial_def, abriendo una clase del metamodelo, pero cual? Podría pensarse de hacerlo sobre Object, al hacerlo sobre esta clase funcionaría para cualquier clase, pero a su vez le estaría dando este comportamiento también a cualquier objeto, lo cual no tendría sentido, sólo lo querríamos para las clases. Una opción válida sería Class aunque si queremos usar partial_def en un módulo no podremos, por lo que la otra opción es hacerlo sobre Module y permitirle tanto a clases como a módulos el de poder utilizar partial_def.

Otra cosa que tuvimos que decidir fue cómo íbamos a representar a los multimethods y cómo almacenar la información de cada definición que se haga usando partial_def. Una primer idea fue tener un atributo que guardara un diccionario donde el selector del mensaje a definir fuera la clave y se le asociara una lista con los partial blocks. Otra alternativa, por la que decidimos ir, era reificar la idea de Multimethod, de esa forma el atributo que terminamos llamando @actual_multimethods tendría directamente una lista de instancias de Multimethod (una por cada selector) de modo que se pudiera delegar también a estos objetos en vez de mantener toda la lógica en Module.

Para poder mandarle el mensaje definido usando partial_def a las instancias de la clase también surgieron ideas distintas. Una de ellas era definir un método en la clase/módulo que recibió partial_def que se llame igual que el símbolo recibido por parámetro de modo que triggeree la búsqueda de la implementación correspondiente en base a los parámetros que reciba, la otra era redefinir method_missing de modo que obtenga el multimethod con el símbolo correspondiente al mensaje no entendido y luego triggeree esa misma búsqueda en base a los parámetros recibidos.

Fuimos por la primer alternativa porque no hay una verdadera necesidad de caer en el method_missing, después de todo ya sabemos de antemano cuál es el mensaje que tiene que poder entender, y en general vamos a optar por no usar method_missing en esos casos ya que es más complejo (tenemos que asegurarnos de mantener consistente la interfaz de reflection también, cosa que si definimos el método usando define_method se da solo).

Además definimos la lógica necesaria para poder responder a los mensajes multimethod y multimethods:

~~~ruby
A.multimethods() #[:concat]
A.multimethod(:concat) #Representación del multimethod
~~~

... cuya implementación, al modelar al multimethod como un objeto, es trivial. Finalmente llegamos al siguiente código:

~~~ruby
class Module

  def partial_def(sym, types, &block)
    partial_block = PartialBlock.new(types, &block)
    multimethod = get_multimethod(sym)
    multimethod.definitions << partial_block
    self.send(:define_method, sym) do |*args|
      multimethod.call(*args)
    end
  end

  def actual_multimethods
    @actual_multimethods ||= []
  end

  def multimethod(sym)
    self.actual_multimethods.find { |mm| mm.selector.eql?(sym) }
  end

  def multimethods
    self.actual_multimethods.map { |mm| mm.selector }
  end

  private

  def has_multimethod?(multimethod)
    self.actual_multimethods.include?(multimethod)
  end

  def get_multimethod(sym)
    multimethod = self.multimethod(sym) || MultiMethod.new(sym)
    actual_multimethods << multimethod unless has_multimethod?(multimethod)
    multimethod
  end

end

class MultiMethod

  attr_accessor :selector, :definitions

  def initialize(sym)
    self.selector = sym
    self.definitions = []
  end

  def call(*args)
    definition = self.definitions
                     .select { |definition| definition.matches(*args) }
                     .min_by { |definition| definition.distance_to(*args) }
    definition ? definition.call(*args) : raise(NoMethodError)
  end

end

class PartialBlock
  def distance_to(*args)
    args.zip(types).each_with_index do |tuple, index|
      case tuple[1]
        when Array then
          1 #because classroom-related reasons
        else
          tuple[0].class.ancestors.index(tuple[1]) * index
      end
    end
  end
end
~~~

Cabe destacar que esa solución de partial_def fue posible gracias a que el bloque conoce el contexto en el cual fue creado, por eso no es necesario buscar el multimethod en la lista.

A su vez, se pide extender la interfaz de reflection con métodos que indiquen si un objeto responde a un determinado mensaje con cierta firma:

~~~ruby
A.new.respond_to?(:concat) # true, define el método como multimethod
A.new.respond_to?(:to_s) # true, define el método normalmente
A.new.respond_to?(:concat, false, [String,String]) # true, los tipos coinciden
A.new.respond_to?(:concat, false, [Integer,A]) # true, matchea con [Object, Object]
A.new.respond_to?(:to_s, false, [String]) # false, no es un multimethod
A.new.respond_to?(:concat, false, [String,String,String]) # false, los tipos no coinciden
~~~

Entonces también debemos redefinir el respond_to?. Hay que tener siempre cuidado con este tipo de extensiones ya que debemos estar atentos de no modificar el comportamiento para aquellos métodos que no fueron definidos por medio de un partial_def. Las opciones propuestas fueron:
Definir respond_to? usando partial_def, de modo que que la definición original de respond_to? (la cual deberíamos asegurarnos de no perder mediante un alias o pidiendo el unbound method y guardándolo en una variable para poder invocarlo más adelante) se use si matchea con los tipos [Symbol] o [Symbol, Object], y una tercer definición para [Symbol, Object, Array] que haga lo que nosotros queremos.
Redefinir respond_to? como un método normal con un if, de modo que si nos pasan el tercer parámetro, se use la definición para multimethods y sino la original.

Tratamos de ir por la primera porque era más divertida, pero lamentablemente no funcionó por un loop infinito (respond_to? se usa en el core del method lookup, no fue por un error de la solución en sí, simplemente justo con en respond_to? no se puede, se las dejamos comentada de todos modos). Luego fuimos por la otra alternativa:
 
~~~ruby
class Object

  def respond_to?(sym, include_private = false, signature = nil)
    signature.nil? ? super(sym, include_private) : self.class.actual_multimethods
               .any? { |mm| mm.matches?(sym, signature) }
  end

=begin
  partial_def :respond_to?, [Symbol] do |sym|
    self.old_respond_to?(sym)
  end
  partial_def :respond_to?, [Symbol, Object] do |sym, bool|
    self.old_respond_to?(sym, bool)
  end
  partial_def :respond_to?, [Symbol, Object, Array] do |sym, bool, types|
    false unless self.class.multimethods.include?(sym)
    multimethod = self.class.multimethod(sym)
    multimethod.matches_signature?(types)
  end
=end

end
~~~

Para saber si existe alguna definición para el multimethod cuya firma matchee con la lista de tipos recibida refactorizamos un poco PartialBlock para evitar la repetición de lógica.

~~~ruby
class Multimethod

  def matches?(sym, types)
    self.selector.eql?(sym) && self.definitions
                             .any? { |definition|definition.matches_signature?(types)}
  end
  
end

class PartialBlock
  def matches(*args)
    arg_types = args.map { |arg| arg.class }
    matches_signature?(arg_types)
  end

  def matches_signature?(signature)
    return false unless signature.size.eql?(self.types.size)
    self.types.zip(signature).all? do |my_type, sign_type| sign_type <= my_type end
  end
end
~~~

Algo que quedó en el tintero para poder ir al siguiente punto es la aclaración de que se pueda usar self dentro de la definición de un método declarado mediante partial_def. Con el código actual eso no funcionará como querríamos, si te animás, c

3. Hay muchos más puntos en el enunciado de TP grupal original, pero por cuestiones de tiempo los dejamos afuera para resolver que pueda usarse duck typing (que fue el TP individual que se tomó), que es básicamente que pueda conocerse la usabilidad de un objeto de acuerdo a sus comportamientos en vez de su tipo estrictamente. Es decir que trato a dos objetos de distintas clases polimórficamente si entienden el mismo subconjunto de mensajes aún si son cosas totalmente diferentes como el siguiente ejemplo:

~~~ruby
class Duck:
    def quack(self):
        print("Quaaaaaack!")
    def feathers(self):
        print("The duck has white and gray feathers.")

class Person:
    def quack(self):
        print("The person imitates a duck.")
    def feathers(self):
        print("The person takes a feather from the ground and shows it.")
    def name(self):
        print("John Smith")

def in_the_forest(duck):
    duck.quack()
    duck.feathers()

def game():
    donald = Duck()
    john = Person()
    in_the_forest(donald)
    in_the_forest(john)

game()
~~~

Ahora volviendo al ejercicio, vamos a definir duck typing de la siguiente manera

~~~ruby
class B
    partial_def :concat, [String, [:m, :n], Integer] do |o1, o2, o3|
    'Objetos Concatenados'
    end
end
~~~

Si el argumento que debería ser un tipo es un array de símbolos, representando el nombre de los selectores que debería entender, entonces se debe aplicar duck typing. Para implementar este agregado entonces debemos extender el matches del partial block de la siguiente manera.

~~~ruby
class PartialBlock
  def matches_signature?(signature)
    return false unless signature.size.eql?(self.types.size)
    self.types.zip(signature).all? do |my_type, sign_type|
      case my_type
        when Array then
          my_type.all? { |method| sign_type.instance_methods.include?(method) }
        else
          sign_type <= my_type
      end
    end

  end
end
~~~

A esta altura debería volverse más evidente que el refactor que hicimos en el punto anterior para no repetir lógica era muy importante. Si no lo hacíamos antes, lo íbamos a tener que hacer ahora.

Algunas alternativas que no hubieran estado buenas para resolver este ejercicio:
Definir todo en términos de duck typing obteniendo todos los mensajes que definen las clases correspondientes. Eso rompería la funcionalidad porque el tipo en base a los mensajes que entiene puede abarcar objetos que no están en la jerarquía que se pedía inicialmente.
Resolver el if/switch con polimorfismo abriendo Array y Module. Esto es algo muy particular de nuestro framework, y ensuciar más la interfaz de Array y Module para evitar ese if no es una buena idea.
