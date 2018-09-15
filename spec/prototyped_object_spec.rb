require 'rspec'
require_relative '../src/prototyped_object'
require_relative '../src/prototyped'
require_relative '../src/prototyped_constructor'

def create_guerrero
  guerrero = PrototypedObject.new
  guerrero.set_property(:energia, 100)
  guerrero.set_property(:potencial_ofensivo, 40)
  guerrero.set_property(:potencial_defensivo, 20)

  guerrero.set_method(:atacar_a, proc {
      |otro_guerrero|
    diff = self.potencial_ofensivo - otro_guerrero.potencial_defensivo
    if (diff > 0)
      otro_guerrero.recibir_danio(diff)
    end
  })

  guerrero.set_method(:recibir_danio, proc {
      |danio| self.energia = [self.energia - danio, 0].max
  })
  guerrero
end

GuerreroConstructor = PrototypedConstructor.new(create_guerrero) do |una_energia, un_potencial_ofensivo,
    un_potencial_defensivo|
  self.energia = una_energia
  self.potencial_ofensivo = un_potencial_ofensivo
  self.potencial_defensivo = un_potencial_defensivo
end

describe 'prototyped behaviour' do

  it 'should be able to define properties' do
    guerrero = PrototypedObject.new
    guerrero.set_property(:energia, 100)

    expect(guerrero.energia).to eq(100)
  end

  it 'should be able to define methods' do
    guerrero = create_guerrero

    otro_guerrero = guerrero.clone
    guerrero.atacar_a(otro_guerrero)

    expect(guerrero).to respond_to(:atacar_a)
    expect(otro_guerrero.energia).to eq(80)
  end

  it 'should be able to set prototype' do
    guerrero = create_guerrero
    espadachin = PrototypedObject.new

    espadachin.set_prototype(guerrero)

    expect(espadachin).to respond_to(:energia)
    expect(espadachin).to respond_to(:potencial_ofensivo)
    expect(espadachin).to respond_to(:potencial_defensivo)
    expect(espadachin).to respond_to(:atacar_a)
    expect(espadachin).to respond_to(:recibir_danio)
  end

  it 'should be able to add methods after setting prototype' do
    guerrero = create_guerrero
    espadachin = PrototypedObject.new

    espadachin.set_prototype(guerrero)
    espadachin.energia = 80

    guerrero.set_method(:sanar, proc {
      self.energia = self.energia + 10
    })

    expect(espadachin).to respond_to(:sanar)

    espadachin.sanar

    expect(espadachin.energia).to eq(90)
  end

  it 'should not affect the prototype when changing the prototyped' do
    guerrero = create_guerrero
    espadachin = PrototypedObject.new

    espadachin.set_prototype(guerrero)

    espadachin.set_method(:habilidad, proc {
      0.5
    })

    expect(espadachin).to respond_to(:habilidad)
    expect{guerrero.habilidad}.to raise_error(NoMethodError)
  end

  it 'should not affect the prototype when changing the prototyped' do
    guerrero = create_guerrero
    espadachin = PrototypedObject.new

    espadachin.set_prototype(guerrero)

    espadachin.set_method(:potencial_ofensivo, proc {30})

    expect(espadachin.potencial_ofensivo).to eq(30)

    guerrero.set_method(:potencial_ofensivo, proc{1000})

    expect(guerrero.potencial_ofensivo).to eq(1000)
    expect(espadachin.potencial_ofensivo).to eq(30)
  end

  it 'should be able to be included in any object' do
    c = Class.new {
      include Prototyped
    }

    perro = c.new
    perro.set_method(:ladrar, proc{"guau"})
    expect(perro.ladrar).to eq("guau")
  end

  it 'should be able to set properties with syntax sugar' do
    guerrero = PrototypedObject.new
    guerrero.energia = 100

    expect(guerrero.energia).to eq(100)
  end

  it 'should be able to set methods with syntax sugar' do
    guerrero = PrototypedObject.new
    guerrero.saludar=(proc {|saludado| "hola #{saludado}"})

    expect(guerrero.saludar("pepe")).to eq("hola pepe")
  end

  it 'should be able to create a constructor with syntax sugar' do
    atila = GuerreroConstructor.new(100, 50, 30)

    expect(atila.energia).to eq(100)
    expect(atila.potencial_ofensivo).to eq(50)
    expect(atila.potencial_defensivo).to eq(30)

    expect(atila).to respond_to(:atacar_a)
    expect(atila).to respond_to(:recibir_danio)
  end

  it 'should be able to create a constructor extending another constructor' do
    EspadachinConstructor = GuerreroConstructor.extended {
      |habilidad, potencial_espada|
      self.habilidad = habilidad
      self.potencial_espada = potencial_espada
      self.set_method(:potencial_ofensivo, proc {
        @potencial_ofensivo + self.potencial_espada * self.habilidad
      })
    }

    zorro = EspadachinConstructor.new(100, 30, 20, 0.5, 20)

    expect(zorro.energia).to eq(100)
    expect(zorro.potencial_ofensivo).to eq(40)
    expect(zorro.potencial_defensivo).to eq(20)
    expect(zorro.habilidad).to eq(0.5)
    expect(zorro.potencial_espada).to eq(20)
  end

end