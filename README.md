# TADP Clase 4: instance_eval, method_missing

### Contexto (scope)
Consideremos el siguiente ejemplo
```ruby
x = 10
class A
  puts x
end
# NameError: undefined local variable or method `x' for A:Class

def m
  puts x
end
m # NameError: undefined local variable or method `x' for main:Object
```

Esto pasa porque la variable 'x' solo puede ser accedida desde el contexto donde se creó.
El contexto cambia en tres casos (se los suele llamar _scope gates_): 
- Cuando se define una clase con class
- Cuando se define un módulo con module
- Cuando se define un método con def

### Flat scope

Una forma de saltear la restricción de los cambios de contexto, es usar closures.
Ruby tiene tres tipos de closures: los bloques, lambdas y procs. Su principal característica es que "recuerdan" el contexto donde fueron creados.
```ruby
a = 5
p = lambda {a = a + 1}
p.call # 6
p.call # 7

a   # 7
```

Para hacer que una variable esté en el contexto de la definición de un método, podemos reemplazar def con define_method.
```ruby
x = 10
define_method(:m) do
  x + 5
end
m # 15
```

De la misma manera, podemos superar el scope gate que define class reemplazandolo con Class.new
```ruby
x = 10
una_clase = Class.new do
  x += 5
end
x # 15
```

A esta técnica que permite mantener el mismo contexto se la llama flat scope (contexto aplanado).

### Bloques / lambdas / procs
Las lambdas y los procs son objetos que reifican comportamiento y pueden ser ejecutados diferidamente.
Tienen dos diferencias fundamentales:
* Cómo manejan los parámetros
* Cómo se comportan con el return
```ruby
lam = lambda { |x| puts x }    # creates a lambda that takes 1 argument
lam.call(2)                    # prints out 2
lam.call                       # ArgumentError: wrong number of arguments (0 for 1)
lam.call(1,2,3)                # ArgumentError: wrong number of arguments (3 for 1)

proc = Proc.new { |x| puts x } # creates a proc that takes 1 argument
proc.call(2)                   # prints out 2
proc.call                      # returns nil
proc.call(1,2,3)               # prints out 1 and forgets about the extra arguments
---------------------------------------------------------------------------------------------
def lambda_test
  lam = lambda { return }
  lam.call
  puts "Hello world"
end

lambda_test                 # calling lambda_test prints 'Hello World'


def proc_test
  proc = Proc.new { return }
  proc.call
  puts "Hello world"
end

proc_test                 # calling proc_test prints nothing
```

Los bloques (tanto si los escribimos con llaves como con do y end) no son objetos y sólo se puede pasar un bloque como último parámetro del método.

```ruby   
def bloque_test
  yield(3)
end

bloque_test do |x|
  x + 2
end
```

Cuando sea necesario, se puede pasar un proc en lugar de un bloque usando &
```ruby   
def bloque_proc_test(&bloque)
  bloque.call(3)
end

bloque_test do |x|
  x + 2
end
```

### Contexto y receptor implícito: instance_eval
Estamos acostumbrados a que, dentro de un método de una clase, podemos mandar un mensaje al objeto actual sin definir ningún receptor.
Por ejemplo: 
```ruby
class Usuario
  attr_accessor :edad

  def initialize(edad)
    @edad = edad
  end

  def mayor_de_edad?
    edad >= 18
  end
end

Usuario.new(19).mayor_de_edad?
# true
```

Para mandar el mensaje `edad` en el método `mayor_de_edad?` no necesitamos poner `self.edad` ya que `self` es el contexto implícito.

#### Contexto y bloques
¿Cuál es el contexto dentro de un bloque? Los mensajes dentro de un bloque tienen receptor implícito?
```ruby
class Usuario
  def lazy_edad
    proc { edad }
  end
end

Usuario.new(19).lazy_edad.call
# 19
```

¿Y si ese bloque lo invoco dentro de otro usuario?
```ruby
class Usuario
  def con_bloque(bloque)
    bloque.call
  end
end

mayor = Usuario.new(19)
menor = Usuario.new(15)
menor.con_bloque(mayor.lazy_edad)
# 19
```

El contexto implícito de un bloque es el mismo que el que existía al momento de ser creado.
Si necesito que el bloque mande mensajes a otro usuario, voy a tener que pasarlo por parámetro:
```ruby
class Usuario
  def edad_de
    proc { |u| u.edad }
  end
end

Usuario.new(19).edad_de.call(Usuario.new(15))
# 15
```

#### Cambiar el contexto
Si pudiera cambiar el receptor default de los bloques, podría evitar pasar por parámetros el destino de mis mensajes!

```ruby
class Usuario
  def edad_de
    proc { edad }
  end
end

bloque = menor.edad_de
mayor.instance_eval(&bloque)
# 19
menor.instance_eval(&bloque)
# 15
```

##### instance_eval vs instance_exec:
- `instance_eval`: cambiar el contexto de un bloque sin parámetros
- `instance_exec`: cambiar el contexto pudiendo pasar parámetros al bloque

##### instance_eval vs class_eval vs module_eval:
Cuando creamos un método dentro del bloque usando `def`:
- `instance_eval`: lo define en la singleton class del objeto
- `module_eval`, `class_eval`: lo agrega como parte de los instance methods de la clase o el modulo.

#### Ejercicio con el Age

##### Bloques sin parámetros

Usando el código del Peloton, definamos estrategias con bloques sin pasarle ningún parámetro.

```ruby
def self.cobarde(integrantes)
 new(integrantes) { retirate }
end

def self.descansador(integrantes)
 new(integrantes) { descansar }
end

def lastimado
 instance_eval(&estrategia)
end
```

##### Definir nuevos métodos

Ya tenemos dos factory methods para construir distintos tipos de Peloton: descansador y cobarde. Queremos tener una forma de definir dinámicamente métodos similares con distintas estrategias. Uso:

```ruby
 Peloton.definir :descansador_cobarde do 
          descansar
          retirate
 end
```

Tiene que definir un método de clase en Peloton:

```ruby  
  un_peloton = Peloton.descansador_cobarde integrantes

  def self.definir(nombre, &estrategia)
    self.define_singleton_method nombre do |integrantes|
      self.new(integrantes, &estrategia)
    end
  end
```

En este caso self es la clase Peloton, por lo que define_singleton_method va a definir un método en la singleton class de Peloton(#Peloton).

### Mensajes dinámicos: method_missing
Por un momento recordemos el patrón decorator. Queremos construir un decorator que retorne retorne "Anon" para los nombres de las personas pero que siga pudiendo acceder al resto de su comportamiento.

La forma clásica de solucionar este problema es redefinir todos los mensajes que entiende la persona en el objeto decorador.
Aquellos que tienen que mantener la lógica del objeto de origen, solo se pasa el mensaje hacia él. En los mensajes que deben cambiar de comportamiento se agrega la nueva lógica.

Sin embargo, ésta lógica repite mucho código y es muy fragil (si la persona modifica los mensajes que entiende, el decorador debe ser adaptado también).

Si pudiéramos capturar todos los mensajes que se envían a un objeto podríamos generalizar esa lógica sin repetir código.
Ruby (y otros lenguajes) nos permite hacer esto mediante el mensaje "method_missing".

```ruby
class Persona
  attr_accessor :nombre, :edad
  def initialize(nombre, edad)
    @nombre = nombre
    @edad = edad
  end
end

class Anonimo
  def initialize(persona)
    @persona = persona
  end

  def method_missing(symbol, *args, &block)
    @persona.send(symbol, *args, &block)
  end

  def nombre
    "Anon"
  end
end

p = Persona.new("Pablo", 32)
p.nombre
# Pablo
ap = Anonimo.new(p)
ap.nombre
# Anon
ap.edad
# 32
```

#### Ejercicio con el Age
Ahora quiero poder tener estos métodos pero usando una convención.

Quiero que la clase Peloton entienda los mensajes con la siguiente forma: `estrategia_<nombre de mensaje>`. Al enviarlo, va a retornar un pelotón que se enviará a si mismo el mensaje `<nombre de mensaje>` al ser lastimado.
```ruby
def self.method_missing(symbol, *args, &block)
 if (symbol.to_s.start_with?('estrategia_') && args.length == 1)
    # mejorar error si lo invoco con diferente cantidad de parámetros
   message = symbol.to_s.gsub('estrategia_', '').to_sym
   Peloton.new(args[0]) {
    send(message)
   }
 else
   super
 end
end
```

Redefinir method_missing tiene un efecto no deseado, Peloton ahora entiende mensajes pero respond_to? de los mismos retorna false.
Para mitigar este problema, el contrato cuando se redefine method_missing es que hay que redefinir respond_to_missing?

```ruby
  def self.respond_to_missing?(sym, priv = false)
    sym.to_s.start_with?('estrategia_')
  end
```

## Anexo
Para definir constantes en ruby, se puede usar Class>>const_set

```ruby
class Guerrero

end

Object.const_set :Atila, Guerrero.new

Atila.atacar(otro)
```

Sólo las clases pueden definir constantes y solo viven en el scope de la clase que la definió.

```ruby
class Bla
  def m
    A
  end
end

Bla.const_set :A, "Bla::A"
Object.const_set :A, "Object::A"

bla = Bla.new
bla.m # "Bla::A"
A # "Object::A"

class Bla
  A # "Bla::A"
end

Bla.class_eval do
  A # "Object::A"
end
```

También existe el método const_missing, que al igual que const_set, lo entienden sólo las clases. Cumple la misma función que method_missing, pero para cuando no se encuentra una constante. 

```ruby
class Bla
  def self.const_missing const
    "#{const} no encontrada"
  end
end

Bla::T # "T no encontrada"
```

## Anexo 2: method_added

Ruby provee un mecanismo para "avisar" cuando se agrega un método en una clase:
```ruby
class A
  def self.method_added(method_name)
    puts "Se agregó el método #{method_name}"
  end
end

class A
  def un_metodo
    # ...  
  end
end
# 'Se agregó el método un_metodo'
```

También se puede saber si se agregó un singleton method:
```ruby
class A
  def singleton_method_added(name)
    puts "Singleton added #{name}"
  end
end

a = A.new

def a.m
  puts 'Soy un a'
end
# 'Singleton added m'
```
