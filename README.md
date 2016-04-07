Clase 2 TADP 2C2016

# Continuación ejercicio age of empires
Sobre el ejercicio de la clase anterior agregamos la posibilidad de que los atacantes y defensores puedan "descansar". [Seguimos este enunciado](https://docs.google.com/document/d/1keATItFXpIC70UeZCZ4wXUKFGIuCF5Uqk_-DWne-hOQ/edit#).

Arranquemos por la implementación en Defensor, cuando un defensor descansa, suma 10 de energía.

```ruby
    module Defensor
    def esta_cansado?
        self.energia < 40
    end

    def descansar
        self.energia += 10
    end
end
```

Por otro lado, cuando le decimos "descansar" al atacante, este ataca con el doble de energía en su próxima pelea.

```ruby
module Atacante


  def atacar(un_defensor)
    if self.potencial_ofensivo > un_defensor.potencial_defensivo
      danio = self.potencial_ofensivo - un_defensor.potencial_defensivo
      un_defensor.sufri_danio(danio)
    end
    @multiplicador = 1
  end

  def potencial_ofensivo
    @potencial_ofensivo * self.multiplicador
  end

  def descansar
    @multiplicador = 2
  end

  def multiplicador
    @multiplicador = @multiplicador || 1
  end

end
```

Hasta acá todo muy bien, pero ahora, surge el problema de que Guerrero, como usa el mixin Atacante, y el mixin Defensor, estaría trayendo dos métodos iguales, por lo que surge un conflicto. Entonces la pregunta es, ¿cómo se va a comportar el guerrero si le mandamos el mensaje descansar?

No se va a romper, porque los conflictos de mixins se resuelven automáticamente por medio de la linearización. En el caso de ruby, la segunda definición va a pisar la primera, entonces por cómo definimos Guerrero, al incluir último el Defensor, la definición del mixin Defensor se va a ejecutar y no la de Atacante.

Pero para los Guerreros vamos a querer que descanse como atacante y luego como defensor (o sea de ambas formas).

Para lograr esto, vamos a usar la posibilidad de crear alias methods que nos provee ruby:

```ruby
class Guerrero
  include Atacante
  alias_method :descansar_atacante, :descansar
  include Defensor
  alias_method :descansar_defensor, :descansar

  def descansar
    self.descansar_atacante
    self.descansar_defensor
  end
end
```

Nótese que no solucionamos el conflicto sólo con definir los alias, lo que vamos a tener son tres métodos: :descansar_atacante, :descansar_defensor y :descansar (cuya implementación va a coincidir con la de Defensor).

Es necesario sobreescribir el método :descansar para lograr el comportamiento que queríamos.

## **Kamikaze**
Ahora queremos agregar los Kamikaze, que son Atacantes y Defensores, pero descansan solo como Atacante porque van a morir de todas maneras:

```ruby
class Kamikaze
  include Defensor
  include Atacante

  def initialize
    @potencial_ofensivo = 250
    @energia = 100
    @potencial_defensivo = 10
  end

  def atacar(un_defensor)
    super(un_defensor)
    @energia = 0
  end
end
```

## **Pelotones**
A continuación vamos a agregar los Pelotones, que tienen un conjunto de Guerreros que lo integran. Queremos que entiendan descansar y que hagan descansar a todos los integrantes que estén cansados. ¿Queremos que el Peloton sea un Defensor o un Atacante? ¿Qué aportan estos Mixins en cuanto a compartición de código? ¿Sería una buena idea desde el punto de vista de la naturaleza?

```ruby
class Guerrero
  attr_accessor :peloton
end

class Peloton
  attr_accessor :guerreros

  def initialize(integrantes)
     self.guerreros = []
     integrantes.each {|integrante| self.agregar_guerrero integrante}
  end

  def agregar_guerrero(guerrero)
    self.guerreros << guerrero
    guerrero.peloton = self
  end

  def descansar
    guerreros.select {|guerrero| guerrero.esta_cansado? }.each { |guerrero| guerrero.descansar }
  end
end
```

Cabe destacar que los bloques (tanto si los escribimos con llaves como con do y end) **no son objetos** y sólo se puede pasar un bloque como último parámetro del método.

Como necesitamos que los guerreros puedan avisarle a su pelotón que sufrieron daño para que el pelotón decida cómo reaccionar, tenemos que redefinir cómo reciben daño los Guerreros.

```ruby
class Guerrero

  def sufri_danio(danio)
    super(danio)
    self.peloton.lastimado
  end

end
```

Ahora, lo que vamos a hacer es modelar las estrategias que sigue el Pelotón, donde cada estrategia es una clase distinta (ver patrón Strategy):

```ruby
class Peloton
  attr_accessor :guerreros, :retirado, :estrategia

  def initialize integrantes, estrategia
    self.guerreros = []
    integrantes.each {|integrante| self.agregar_guerrero integrante}
    self.retirado = false
    self.estrategia = estrategia
  end

  def lastimado
    self.estrategia.lastimado(self)
  end

  def retirate
    self.retirado = true
  end
end

class Descansador
  def lastimado(peloton)
    peloton.descansar
  end
end

class Cobarde
  def lastimado(peloton)
   peloton.retirate
  end
end
```

El problema que tenemos aca es que cada vez que se quiera crear un nuevo tipo de estrategia, debe crearse una nueva clase. Esto en principio no es muy problemático, pero es algo que puede ser molesto si hay muchos tipos de estrategias para cuando se lastima el pelotón, tendría un montón de clases para que definan sólo un método.

Una alternativa para este problema sería que el Peloton conozca un bloque de código en vez de una instancia de Descansador o Cobarde. Si bien los bloques de Ruby no son objetos, y necesitaríamos que lo sean para que el pelotón lo conozca y lo pueda ejecutar cuando sea necesario mandándole un mensaje, lo que podemos usar son procs o lambdas, que **sí son objetos**. Para evitar confusiones a los procs y lambdas vamos a decirles closures, después podemos ver en qué se diferencian pero no hace al ejemplo.

La parte simpática de tener una clase por cada estrategia era la facilidad de creación de un ejército descansador por ejemplo, ya que si tenemos que inicializarlo con el closure adecuado podría llevarnos a repetir lógica. Por eso definimos métodos de clase que devuelven el Peloton creado y configurado con el código de la acción a tomar.

Para definir estos métodos de clase vamos a esperar que nos pasen bloques y en el método convertirlo en un closure que pueda ser referenciado por el pelotón y usado más adelante con el mensaje call.

```ruby
class Peloton
  attr_accessor :guerreros, :retirado, :accion_a_tomar

  def self.cobarde integrantes
    self.new( integrantes ) { |peloton| peloton.retirate }
  end

  def self.descansador integrantes
    self.new( integrantes) { |peloton| peloton.descansar }
  end

  def initialize(integrantes, &accion_a_tomar)
    self.guerreros = []
    integrantes.each {|integrante| self.agregar_guerrero integrante}
    self.retirado = false
    self.accion_a_tomar = accion_a_tomar
  end

  def lastimado(defensor)
    self.accion_a_tomar.call(self)
  end

end
```

Retomando el tema de las diferencias entre procs y lambdas, los procs no checkean la cantidad de argumentos que reciben mientras que las lambdas sí. Por otro lado también hay una diferencia con el uso de return dentro de una lambda o de un proc, ya que en return en la lambda sólo finaliza la ejecución de ella misma sin afectar al contexto en el cual se encuentra, mientras que el proc hace que se retorne del método que lo contiene.

Tanto procs como lambdas son instancias de la clase Proc.

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
------------------------------
a = 5
p = proc {a = a + 1}
p.call # 6
p.call # 7

a   # 7
```

Código de la clase:

[https://github.com/uqbar-paco/tadp-2015c2-age-of-empires](https://github.com/uqbar-paco/tadp-2015c2-age-of-empires)
