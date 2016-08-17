# Clase 5: Ejercicio Integrador

En estas dos clases (clase 5 y 6) estaremos viendo y avanzando con un ejercicio integrador en el que vayamos aplicando los distintos conceptos de metaprogramación y aplicar también lo visto del metamodelo de Ruby. Para ello empezaremos viendo el enunciado del Prototype, que en algún momento fue un TP y ahora lo estamos dando como un ejercicio integrador. 

La resolución a la que llegamos está en el siguiente repositorio (https://github.com/tadp-utn-frba/tadp-clases/tree/ruby-prototype)

## Cuál fue la problemática en la resolución de este enunciado?

Qué sucede cuando se tiene una instancia de PrototypedObject guerrero al cual se le incorpora el comportamiento nuevo modificando su singleton class?

~~~ruby
 @guerrero = PrototypedObject.new
 @guerrero.set_property(:energia, 100)
~~~

Se desea que en algunos casos pueda existir una suerte de herencia con otros prototyped Objects, de modo que hereden el comportamiento que existe en la singleton class de la instancia de PrototypedObject. Se propuso la siguiente interfaz para obtener un nuevo PrototypedObject a partir del prototipo siguiendo la idea del clone de Ruby pero sin modificar el mismo para evitar problemas que podrían surgir por modificar la intención del clone, no sólo la implementación.

~~~ruby
@otro_guerrero = @guerrero.clone_object
~~~

Vimos que usando clone, puedo obtener una nueva instancia de PrototypedObject con el mismo estado y comportamiento pero sin que se mantenga un vínculo con su prototipo (si el mismo cambiase se comportamiento más adelante, el clon no se va a ver afectado), ya que el clon se obtiene mediante un shallow copy del original que sólo copia en la singleton class del nuevo objeto lo que tenía la singleton class del original.
En el test tenemos que otro_guerrero es un clon de guerrero, que es un PrototypedObject. Lo que hicimos para el clone_object antes que nada fue setear el prototipo del nuevo objeto creado con el receptor. 

Si no hacemos más nada cuando querramos utilizar un comportamiento que viene heredado de guerrero en otro_guerrero, nuesto method lookup no estará buscando por la jerarquía de guerrero. 

Salieron distintas alternativas para resolver esto:

- Que tengamos nuestro propio method lookup y en caso de que no encontremos el método al que estemos llamando desde otro_guerrero, en nuestro lookup de prototypes, se siga por el method lookup de Ruby que tenemos. De esta manera, no es invasivo, o sea, que no estamos manoteando el method lookup original y permite que no pase el escenario en el que se pueda romper el entorno utilizando nuestro framework de prototypes con otras herramientas. Lo otro es que tenemos más control sobre lo que está sucediendo, aunque por otro lado empiezan a aparecer la inconsistencias, con las interfaces de reflection que tenemos en Ruby ya que es un method lookup independiente, y en ese caso para obtener comportamiento que está definido en nuestro method lookup necesitaremos exponer una interfaz aparte para conocer sobre ese mecanismo alternativo, por lo que implica mucho más trabajo adicional y manual.
- Que el prototipo conozca a todos los que fueron clonados a partir de él y al momento de sufrir cambios avisarle a todos sus clones que tienen que cambiar. Esta alternativa tampoco es invasiva, pero tiene varios problemas asociados desde el punto de vista de performance cada vez que se quiere hacer un cambio y la complejidad algorítmica que puede surgir ya que nada nos evita que se generen loops en el grafo de objetos con los prototipos.
- Modificar el método lookup de Ruby, haciendo que la eigenclass de la instancia de otro guerrero extienda de la eigenclass de guerrero,  de esta manera no necesitamos hacer mucho trabajo y existe una consistencia, aunque por otra parte sucede que Ruby no nos permite de manera fácil hacer estos cambios en el metamodelo (no se permite crear una clase como subclase de una eigenclass).
- Se puede hacer que los prototipos se incluyan por medio de módulos, y de esta manera estamos cambiando el método lookup de una manera que nos provee Ruby ya mediante la inclusión de módulos. Esta es probablemente la solución más simple y consistente de todas, ya que al ajustar nuestras ideas para que calcen a lo que Ruby ya soporta, todas las herramientas asociadas (como el respond_to?) ya van a funcionar como deberían sin que hagamos nada.

Continuamos la clase con la opción de hacer un method lookup paralelo redefiniendo method_missing, no porque fuera la mejor opción sino porque era interesante de analizar.

~~~ruby
def respond_to?(sym)
 super or self.prototype.respond_to? sym
end

def method(sym)
 begin
   super
 rescue NameError
   self.prototype.method sym
 end
end

def method_missing(sym, *args)
 super unless respond_to? sym
 method = self.prototype.method(sym).unbind
 method.bind(self).call *args
end
~~~

De esta manera cuando querramos ejecutar un comportamiento de otro_guerrero que está definido en guerrero (su prototipo), después de buscar por el método lookup de Ruby, llega al method missing donde hacemos que busque en el prototipo. La forma en la cual podemos obtener el comportamiento correspondiente del mensaje no entendido es mandarle el mensaje method al prototipo, pero ese método va a estar bindeado al prototipo, motivo por el cual habría que desbindearlo y bindearlo al objeto otro_guerrero; pero al hacer esto nos va a tirar el error de que no se puede bindear un Unbound Method de una instancia de una jerarquía al de otra jerarquía, esto es por una limitación en la implementación del lenguaje.
Es por esto que se deberá optar por otro mecanismo aún si en teoría lo que propusimos puede funcionar perfectamente si no estuviese esta restricción.

El mismo problema hubiera surgido si en vez de pedírselo a la instancia y luego mandarle unbind se lo hubiéramos pedido a su eigenclass mandándole el mensaje instance_method, ya que nos retornaría ya un UnboundMethod pero tiraría un error al tratar de bindearlo con otro_guerrero.

La resolución final esta en el siguiente [repositorio](https://github.com/tadp-utn-frba/tadp-clases/tree/ruby-prototype).


