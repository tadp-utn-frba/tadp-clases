require 'rspec'
require_relative '../src/prototype'

RSpec.describe 'Prototype' do
  it "puedo ponerle y pedirle propiedades a los objetos" do
    guerrero = PrototypedObject.new

    guerrero.set_property(:energia, 100)

    expect(guerrero.get_property(:energia)).to eq(100)
    expect(guerrero.get_property(:vida)).to be nil # ?
  end

  it "las propiedades de un prototyped object son solo de ese prototyped object" do
    guerrero = PrototypedObject.new
    alquimista = PrototypedObject.new

    guerrero.set_property(:poder_ofensivo, 100)

    expect(alquimista.get_property(:poder_ofensivo)).to be nil
  end

  it "setear una property genera un metodo" do
    guerrero = PrototypedObject.new

    guerrero.set_property(:energia, 100)

    expect(guerrero.energia).to eq 100
  end

  it "se pueden definir metodos con set_method" do
    guerrero = PrototypedObject.new
    guerrero.set_method(:recibe_danio, proc do |danio|
      nueva_energia = self.energia - danio
      set_property(:energia, nueva_energia)
    end)

    guerrero.set_property(:energia, 100)
    guerrero.recibe_danio(10)

    expect(guerrero.energia).to eq 90
  end

  it "un objeto prototipico puede usar los metodos de su prototipo" do
    guerrero = PrototypedObject.new
    guerrero.set_method(:recibe_danio, proc do |danio|
      nueva_energia = self.energia - danio
      set_property(:energia, nueva_energia)
    end)

    espadachin = PrototypedObject.new
    espadachin.set_property(:energia, 50)
    espadachin.set_prototypes([guerrero])

    espadachin.recibe_danio(20)
    expect(espadachin.energia).to eq(30)
  end

  it "multiples prototipos" do
    guerrero = PrototypedObject.new
    alquimista = PrototypedObject.new

    guerrero.set_method(:hola, proc { "HOLA" })
    alquimista.set_method(:hola, proc {"se te apetece alguna de mis pociones" })

    coso = PrototypedObject.new
    coso.set_prototypes([guerrero, alquimista])

    expect(coso.hola).to eq "HOLA"
  end
end
