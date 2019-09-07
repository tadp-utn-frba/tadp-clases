require_relative 'age'
atila = Guerrero.new
atila.class
atila.class.superclass

atila.methods 
Guerrero.instance_methods
Guerrero.instance_methods(false)

# Envio de mensajes

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

# bound/unbound method

metodo = atila.method(:potencial_ofensivo)
metodo.call

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

metodo = atila.method(:atacar)
metodo.arity
metodo.parameters
metodo.owner

# variables

atila.instance_variables
atila.instance_variable_get(:@energia)
atila.instance_variable_set(:@energia, 50)
atila.instance_variable_get(:@energia)
atila

# Open Classes

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
  2+2

#define method

Guerrero.send(:define_method, :saluda) {
    'Hola'
}
Guerrero.new.saluda  #=> "Hola"

# define singleton method

atila.define_singleton_method(:saluda) {
  'Hola soy Atila'
}
atila.saluda
Guerrero.new.saluda


#### Segunda parte......

zorro = Espadachin.new(Espada.new(123))

# Eigenclasses

Peloton.cobarde([])

Peloton.methods.include? :cobarde  #=> true
Peloton.class.instance_methods.include? :new  #=> true
Peloton.class.instance_methods.include? :cobarde  #=> false

Peloton.singleton_class.instance_methods(false)  #=> [:cobarde, :descansador]

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

##

atila = Guerrero.new
atila.singleton_class.send(:define_method, :comerse_un_pollo, proc { @energia += 20 })
atila.energia
atila.comerse_un_pollo
atila.energia
Guerrero.new.comerse_un_pollo  # NoMethodError

##

atila.singleton_class.send(:attr_accessor, :edad)
atila.edad = 5
atila.edad  #=> 5
Guerrero.new.edad  #=> NoMethodError

Guerrero.ancestors  #=> [Guerrero, Defensor, Atacante, Object, PP::ObjectMixin, Kernel, BasicObject]
Guerrero.new.singleton_class.ancestors

class Guerrero
  def self.gritar
       'haaaa'
  end
end

atila.gritar  #=> NoMethodError
Guerrero.gritar  #=> haaaa

Espadachin.gritar  #=>haaaa