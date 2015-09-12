require 'rspec'
require_relative '../src/prototype'

describe 'Testear los distintos puntos del prototype' do

  before do
    @guerrero = PrototypedObject.new
    @guerrero.set_property(:energia, 100)
  end

  it 'Seteo una propiedad y pido su valor' do
    expect(@guerrero.energia).to eq(100)
  end

  it 'Entiende descansar' do
    @guerrero.set_method :descansar, proc {
                                     self.energia += 20
                                   }

    expect(@guerrero.respond_to? :descansar)
    expect(@guerrero.descansar).to eq(120)
  end

  it 'Un guerrero ataca a otro' do
    @guerrero.set_property(:potencial_defensivo, 10)
    @guerrero.set_property(:potencial_ofensivo, 30)

    @guerrero.set_method(:atacar_a,
                         proc {
                             |otro_guerrero|
                           if otro_guerrero.potencial_defensivo < self.potencial_ofensivo
                             otro_guerrero.recibe_danio(self.potencial_ofensivo - otro_guerrero.potencial_defensivo)
                           end
                         })

    @guerrero.set_method(:recibe_danio, proc { |delta|
                                        self.energia -= delta
                                      })

    @otro_guerrero = @guerrero.clone_object

    @guerrero.atacar_a @otro_guerrero

    expect(@otro_guerrero.energia).to eq(80)

  end

  it 'Prototipar espadachin a partir de guerrero' do
    @guerrero.set_method(:atacar_a,
                         proc {
                             |otro_guerrero|
                           if otro_guerrero.potencial_defensivo < self.potencial_ofensivo
                             otro_guerrero.recibe_danio(self.potencial_ofensivo - otro_guerrero.potencial_defensivo)
                           end
                         })

    @guerrero.set_method(:recibe_danio, proc { |delta|
                                        self.energia -= delta
                                      })

    @otro_guerrero = @guerrero.clone #clone es un metodo que ya viene definido en Ruby

    espadachin = PrototypedObject.new

    espadachin.set_prototype(@guerrero)

    espadachin.set_property(:habilidad, 0.5)
    espadachin.set_property(:potencial_espada, 30)

    espadachin.set_property(:potencial_defensivo, 10)
    espadachin.set_property(:potencial_ofensivo, 20)

    espadachin.energia = 100

    #deberia llamar a super, pero eso lo resolvemos mas adelante
    espadachin.set_method(:potencial_ofensivo, proc {
                                               @potencial_ofensivo + self.potencial_espada * self.habilidad
                                             })

    espadachin.atacar_a(@otro_guerrero)
    expect(@otro_guerrero.energia).to eq(75)

  end

  it 'despues del clone los nuevos refinamientos no importan' do
    #   @guerrero = PrototypedObject.new
    #
    #   @guerrero.set_property(:energia, 100)
    #   @guerrero.set_property(:potencial_defensivo, 10)
    #   @guerrero.set_property(:potencial_ofensivo, 30)
    #
    #   @guerrero.set_method(:atacar_a,
    #                        proc {
    #                            |otro_guerrero|
    #                          if otro_guerrero.potencial_defensivo < self.potencial_ofensivo
    #                            otro_guerrero.recibe_danio(self.potencial_ofensivo - otro_guerrero.potencial_defensivo)
    #                          end
    #                        })
    #
    #   @guerrero.set_method(:recibe_danio, proc { |delta|
    #                                       self.energia -= delta
    #                                     })
    #
    #   @otro_guerrero = @guerrero.clone #clone es un metodo que ya viene definido en Ruby
    #
    #   espadachin = PrototypedObject.new
    #   espadachin.set_prototype(@guerrero)
    #   espadachin.energia= 100
    #
    #   @guerrero.set_method :sanar, proc {
    #                                self.energia+= 10
    #                              }
    #
    #   expect(@guerrero.sanar).to eq(110)
    #   expect{ @otro_guerrero.sanar}.to raise_error(NoMethodError)
    #   expect(espadachin.sanar).to eq(110)
    # end


  end
end