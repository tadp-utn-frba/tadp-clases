Clase 2 TADP 2C2016

# Continuación ejercicio age of empires
Sobre el ejercicio de la clase anterior agregamos la posibilidad de que los atacantes y defensores puedan "descansar" y los siguientes requerimientos.

El descanso de la guerra

Todas las unidades pueden descansar. Cuando un atacante descansa, tiene el efecto de duplicar su potencial ofensivo en su próximo ataque.
Cuando un defensor descansa, suma siempre 10 de energía.


Banzai!

La característica del kamikaze es que se comporta como un atacante y un defensor, y su potencial ofensivo es 250, pero luego de atacar, su energía queda en 0.
Como la unidad va a morir luego de atacar, puede descansar como atacante, pero no debe descansar como defensor.


Atacando de la mano

Queremos que los guerreros formen parte de un pelotón. Cuando un guerrero es atacado, el pelotón puede tomar alguna acción. Ciertos pelotones se retiran cuando alguna de sus unidades es lastimada. Otros pelotones hacen que el guerrero descanse cuando recibe un daño y no está descansado. Un guerrero está descansado cuando su energía es mayor a 40.

## **Implementación**

Arranquemos por el Defensor, cuando un defensor descansa, suma 10 de energía.

```ruby
module Defensor

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
    self.descansado = false
  end

  def potencial_ofensivo
    self.descansado ? @potencial_ofensivo * 2 : @potencial_ofensivo
  end

  def descansar
    self.descansado = true
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

  def initialize(energia=100, potencial_defensivo=10)     
    self.potencial_ofensivo = 250 
    self.energia = energia     
    self.potencial_defensivo = potencial_defensivo   
  end

  def atacar(un_defensor)
    super(un_defensor)
    self.energia = 0
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
     self.integrantes = integrantes
     self.integrantes.each { |integrante|integrante.peloton = self} 
  end

  def descansar     
    cansados = self.integrantes.select { |integrante| 
      integrante.cansado     
    }     
    cansados.each { |integrante| 
      integrante.descansar 
    }  
  end

end
```

Cabe destacar que los bloques (tanto si los escribimos con llaves como con do y end) **no son objetos** y sólo se puede pasar un bloque como último parámetro del método.

Como necesitamos que los guerreros puedan avisarle a su pelotón que sufrieron daño para que el pelotón decida cómo reaccionar, tenemos que redefinir cómo reciben daño los Guerreros.

```ruby
class Guerrero

  def sufri_danio(danio)
    super(danio)
    self.lastimado if cansado 
  end
  
  def cansado     
    self.energia <= 40
  end 

end
```

Ahora, lo que vamos a hacer es modelar las estrategias que sigue el Pelotón, donde cada estrategia es una clase distinta (ver patrón Strategy):

```ruby
class Peloton
  attr_accessor :integrantes, :retirado, :estrategia

  def initialize(integrantes, estrategia) 
    self.integrantes = integrantes
    self.estrategia = estrategia
    self.integrantes.each { |integrante| 
      integrante.peloton = self
    }
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
  attr_accessor :integrantes, :retirado, :estrategia

  def self.cobarde(integrantes) 
    self.new(integrantes) {|peloton| 
      peloton.retirate
    }
  end

  def self.descansador(integrantes)
    self.new( integrantes) { |peloton| 
      peloton.descansar 
    }
  end

  def initialize(integrantes, &estrategia)
    self.integrantes = integrantes
    self.estrategia = estrategia
    self.integrantes.each { |integrante| 
      integrante.peloton = self
    }
  end

  def lastimado(defensor)
    self.estrategia.call(self)
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

## **Anexo**

**MIXINS**

Si lo que queremos es incluir comportamiento de n modules, pero no nos importa utilizar un método que comparten (en este caso descansar), podemos incluir a los modules de cualquier forma, y no va a influir en lo que hagamos despues. Por el contrario, si lo que queremos es utilizar algun metodo que aparece en mas de un module, tenemos que resolver los conflictos que se presenten.

**MIXINS: Resolución de conflictos**


En este ejemplo, vemos que un Atacante no descansa de la misma forma que un Defensor, por lo cual podemos tomar varios caminos

1) Si lo que queremos es incluir comportamiento de ambos, pero ademas nos interesa que descanse como uno o como el otro, entonces el que nos interesa es el que deberia estar incluido mas abajo, porque al ser su superclase mas inmediata es del primero que toma el metodo cuando no lo encuentra en Guerrero. Esto conlleva a un grave problema de todas formas, ya que si ambos tuviesen n cantidad de metodos que se llaman igual pero por algun motivo hacen cosas diferentes, ya no seríamos libres de elegir cuál toma prioridad por sobre el otro. Aunque estas cosas no suelen pasar con mucha frecuencia, es importante tenerlo en cuenta. En este caso, llegamos a la opcion 2
2) Alias methods. Nos sirven para poder renombrar estos métodos compartidos con otro nombre y así poder utilizarlos, tanto si queremos separar su comportamiento como si queremos juntarlos. En el ejemplo, vemos que les cambia el nombre a ambos metodos para justamente poder usar a los dos. Ahora si, una vez que ya tengo estos metodos separados puedo entonces si quiero definir un nuevo metodo descansar, que ejecute a ambos. 
3) Tambien habiamos visto en clase una alternativa usando super, ya que cuando incluimos a un module por debajo del otro quedan como si fuesen superclases una de la otra (recordar que si hago ancestors de la clase que incluye a los modules, devuelve los modules y los otros ancestors de la clase), y entonces el module que se encuentre mas arriba va a ser la superclase mas lejana. 
En el caso de descansar, podriamos haber definido solo el alias method para Atacante, y que cuando definimos el nuevo descansar ejecute super y luego a descansar_atacante.

Hay que notar que si nosotros hubiesemos incluido más modules, podriamos hacer super (si todos tienen descansar en comun) del ultimo que incluimos, porque es la superclase inmediata, y del resto, definir alias methods.
 Guerrero.ancestors
=> [Guerrero, Defensor, Atacante, Object, PP::ObjectMixin, Kernel, BasicObject]
Aca vemos que su primer Superclase es Defensor.

[38] pry(main)> class Guerrero
[38] pry(main)*   include Atacante
[38] pry(main)*   alias_method :descansar_atacante, :descansar
[38] pry(main)*   include Defensor
[38] pry(main)*   def descansar
[38] pry(main)*     self.descansar_atacante  super
[38] pry(main)*   end  
[38] pry(main)* end  
[39] pry(main)> un_guerrero = Guerrero.new
=> #<Guerrero2:0x0000000258f668>
[40] pry(main)> un_guerrero.descansar
soy atacante
soy defensor

**Lazy Initialization**

Podemos usar ||= para inicializar una variable, pero hay que tomar algunas consideraciones con eso. Hay que tener en cuenta que Ruby tiene algunos valores que considera que son false o que son true. Por ejemplo, nil lo considera false, y otros valores como numeros, letras, etc, los considera true.
Supongamos que quiero inicializar una variable que se llama @a

```
def inicializar
 @a ||= @b
end
```
```
### b. Si @variable es true o se considera true, toma el valor de  @a, sin importar si @b es false o true
[15] pry(main)> @a = 3
=> 3
[16] pry(main)> @b = false
=> false
[17] pry(main)> @a ||= @b
=> 3
[18] pry(main)> @b = 4
=> 4
[19] pry(main)> @a ||= @b
=> 3

### b. Si @a es false o se considera false, toma el valor de @b

 pry(main)> @a = nil
=> nil
[2] pry(main)> @b = 3
=> 3
[3] pry(main)> @a || @b    ## Aca vemos que al hacer || devuelve el que considera true, o sea @b
=> 3
[4] pry(main)> @a   ## @a sigue siendo nil
=> nil
[5] pry(main)> @a ||= @b    ##Lazy initialization
=> 3
[6] pry(main)> @a
=> 3

### c. Si ambos son false o se consideran false, siempre toma el segundo valor.

[12] pry(main)> @a = nil
=> nil
[13] pry(main)> @b = false
=> false
[14] pry(main)> @a ||= @b
=> false
```

## **Sobre bloques como objetos**

Ademas de todo lo visto de bloques, puede ser muy util guardar un bloque como un objeto (un proc) y ya vimos que lo único que hay que hacer es agregar la palabra proc antes del mismo. Esto permite que podamos guardarlo en una variable y que le podamos hacer
@a_block.call(...args...)
Ahora, suponiendo que queremos revertir esto y convertir el objeto que era un bloque, en un bloque de nuevo, lo único que tenemos que hacer es agregar un &, por ejemplo: &@a_block, ya no es un objeto, sino un bloque, pero hay que entender que lo podemos hacer mientras un metodo lo vaya a recibir como argumento. 
Un método puede recibir solo un bloque, pero varios argumentos. Quiere decir que yo puedo definir al metodo un_metodo(a,b, …), pero para poder mandarle un bloque necesito agregar el & al momento de definirlo.

Por ejemplo, 
```
[1] pry(main)> def un_metodo(a,b,&a_block)
[1] pry(main)*   a_block.call
[1] pry(main)* end  
=> :un_metodo
```

Es un metodo que va a recibir dos argumentos y un bloque. Si yo tuviese un proc @a_proc, para pasarselo efectivamente deberia hacer &@a_proc.
Probamos lo que pasa:
```
[2] pry(main)> @a_proc = proc do "hello!" end
=> #<Proc:0x0000000205c600@(pry):101>

### Miren lo que pasa si al metodo le paso un proc:
[3] pry(main)> un_metodo 0, 0, @a_proc
ArgumentError: wrong number of arguments (given 3, expected 2)
from (pry):79:in `un_metodo'

#### En cambio, si le paso un bloque:

[5] pry(main)> un_metodo 0,0,&@a_proc
hello!
=> nil

#### o también

[6] pry(main)> un_metodo(0,0) do puts "hello!" end
hello!
=> nil
```

