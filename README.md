Clase 5 TADP 1C2019

## Assertions

#### [obj].deberia ser [matcher]
```ruby
(2 + 2).deberia ser mayor_a 3
```

#### ser_[condicion]
```ruby
class Persona
  def viejo?
    @edad > 29
  end
end

nico.deberia ser_viejo    # pasa: Nico tiene edad 30.
```

#### tener_[atributo] [matcher] [valor]
```ruby
leandro.deberia(tener_edad(mayor_a(22))) # pasa
leandro.deberia tener_altura mayor_a 123 # falla: no hay atributo altura
```

## Suites y Tests

```ruby
class MiSuite
  # Esto es un test
  def testear_que_las_personas_de_mas_de_29_son_viejas
    persona = Persona.new(30)
    persona.deberia ser_viejo
  end
  
  # Esto no
  def las_personas_de_mas_de_29_son_viejas
    persona = Persona.new(30)
    persona.deberia ser_viejo
  end
end

TADsPec.testear MiSuite
```

## Mocking & Spying

#### Mock

```ruby
class PersonaHome
  def todas_las_personas
    # Este método consume un servicio web que consulta una base de datos
  end

  def personas_viejas
    self.todas_las_personas.select{|p| p.viejo?}
  end
end

it 'testear_que_personas_viejas_trae_solo_a_los_viejos' do
  nico = Persona.new(30)
  axel = Persona.new(30)
  lean = Persona.new(22)

  # Mockeo el mensaje para no consumir el servicio y simplificar el test
  PersonaHome.mockear(:todas_las_personas) do
    [nico, axel, lean]
  end

  expect(PersonaHome.personas_viejas).to eq [nico, axel]
end
```

#### Spy
```ruby
class Persona
  attr_accessor :edad
  
  def initialize(edad)
    @edad = edad
  end
  
  def viejo?
    self.edad > 29
  end
end

class PersonaTest

  def testear_que_se_use_la_edad
    lean = Persona.new(22)
    pato = Persona.new(23)
    pato = espiar(pato)

    pato.viejo?

    pato.deberia haber_recibido(:edad)
    # pasa: edad se llama durante la ejecución de viejo?

    pato.deberia haber_recibido(:edad).veces(1)
    # pasa: edad se recibió exactamente 1 vez.
    pato.deberia haber_recibido(:edad).veces(5)
    # falla: edad sólo se recibió una vez.

    pato.deberia haber_recibido(:viejo?).con_argumentos(19, "hola")
    # falla, recibió el mensaje, pero sin esos argumentos.

    pato.deberia haber_recibido(:viejo?).con_argumentos()
    # pasa, recibió el mensaje sin argumentos.

    lean.viejo?
    lean.deberia haber_recibido(:edad)
    # falla: lean no fue espiado!
  end
end
```

## Ejercicio Integrador
El siguiente ejercicio fue el TP de metaprogramación del 2do cuatrimestre de 2016. Incluye también lo que luego fueron TPs individuales.
Enunciado: https://docs.google.com/document/d/1bP12p1qKRNoGORs3bb1At7qeU4iAk0CHbtYODIN-QLI/edit

Acá está como se resolvió en el otro aula: https://github.com/tadp-utn-frba/tadp-clases/tree/ruby-tadspec
