# tadp-age-instace_eval-method_missing

Continuación del ejercicio de Age of Empires con los pelotones que pueden ser descansadores, cobardes o cualquier otra estrategia usando bloques sin tener que definir explícitamente los métodos de clase.

Se cambian los bloques que reciben a los pelotones por bloques sin parámetros para evaluarlos en el contexto del pelotón, introduciendo instance_eval.

Se usa define_singleton_method para definir los métodos de clase dinámicamente, y como alternativa a usar el método de clase para definir las estrategias, se redefine method_missing para poder definir los métodos de clase con mensajes como :descansador=.


## Script
La clase anterior el código de Peloton quedó así:
~~~ruby
  def lastimado
    self.estrategia.call(self)
  end
~~~

Las estrategias eran bloques que reciben el pelotón por parámetro
~~~ruby
  lambda { |peloton|
      peloton.descansar
   }  
~~~	           

Queremos que no sea necesario pasar el pelotón:
~~~ruby
  def self.descansador(integrantes)
    self.new(integrantes) {
      descansar
    }
  end
~~~
Para lograr esto tenemos que lograr que el bloque se ejecute en el contexto del pelotón, o dicho de otra manera, que dentro del bloque self referencie al pelotón.
Podemos usar instance_eval que justamente sirve para este objetivo.
~~~ruby
  def lastimado
    self.instance_eval &self.estrategia
  end
~~~

Ya tenemos dos factory methods para construir distintos tipos de Peloton: descansador y cobarde. Queremos tener una forma de definir dinámicamente métodos similares con distintas estrategias. Uso:
~~~ruby
 Peloton.definir :descansador_cobarde do 
	          descansar
	          retirate
	        end
~~~
Tiene que definir un método de clase en Peloton:
~~~ruby  
  un_peloton = Peloton.descansador_cobarde integrantes

  def self.definir(nombre, &estrategia)
    self.define_singleton_method nombre do |integrantes|
      self.new(integrantes, &estrategia)
    end
  end
~~~

En este caso self es la clase Peloton, por lo que define_singleton_method va a definir un método en la singleton class de Peloton(#Peloton).

A continuación queremos agregar un pequeño DSL(domain specific language) para crear factory methods con la siguiente sintaxis:
~~~ruby
class Peloton
  descansador do descansar end
  cobarde do retirate end
end
~~~

Si ejecutamos el código anterior, Ruby nos va a tirar el siguiente error:
undefined method `cobarde' for Peloton:Class
Lo que pasó es que intentamos mandarle el mensaje cobarde a Peloton y no estaba definido(teniendo en cuanta que borramos la definición anterior).
Cuando le mandamos a un objeto un mensaje que no entiende, antes de romper se le manda el mensaje method_missing al receptor del mensaje original, cuya definición default es tirar una excepción.
Podemos redefinir method_missing para lograr lo que queríamos:
~~~ruby
  def self.method_missing(symbol, *args, &block)
    self.definir symbol, &block
  end
~~~

Redefinir method_missing tiene un efecto no deseado, Peloton ahora entiende mensajes que no devuelve methods y respond_to? retorna false.
Para mitigar este problema, el contrato cuando se redefine method_missing es que hay que redefinir respond_to_missing?
~~~ruby  
  def self.respond_to_missing?(symbol, include_all=false)
    true
  end
~~~