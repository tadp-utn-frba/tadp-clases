require_relative '../lib/prototype'

def create_guerrero
  guerrero = PrototypedObject.new
  guerrero.set_property(:energia, 100)
  guerrero.set_property(:potencial_ofensivo, 30)
  guerrero.set_property(:potencial_defensivo, 10)

  guerrero.set_method(:atacar_a, proc {
      |otro_guerrero|
    diff = self.potencial_ofensivo - otro_guerrero.potencial_defensivo
    if diff > 0
      otro_guerrero.recibir_danio(diff)
    end
  })

  guerrero.set_method(:recibir_danio, proc {
      |danio| set_property(:energia, [self.energia - danio, 0].max)
  })
  guerrero
end

describe 'Prototipos' do
  describe 'Un prototipo' do
    it 'deberia poder agregar una property' do
      guerrero = PrototypedObject.new
      guerrero.set_property(:sabiduria, 50)

      expect(guerrero.get_property(:sabiduria)).to eq 50
      expect(PrototypedObject.new
                               .get_property(:sabiduria))
        .to eq nil
    end

    it "se pueden definir metodos con set_method" do
      guerrero = PrototypedObject.new
      guerrero.set_method(:recibe_danio,
                          proc do |danio|
        nueva_energia = self.energia - danio
        set_property(:energia, nueva_energia)
      end)

      guerrero.set_property(:energia, 100)
      guerrero.recibe_danio(10)

      expect(guerrero.energia).to eq 90
    end

    it "se pueden definir metodos con set_method" do
      guerrero = create_guerrero
      otro_guerrero = guerrero.clone

      guerrero.atacar_a otro_guerrero
      expect(otro_guerrero.energia).to eq 80
      expect(guerrero.energia).to eq 100
    end

    it "un objeto prototipico puede usar los metodos de su prototipo" do
      guerrero = create_guerrero

      espadachin = PrototypedObject.new
      espadachin.set_property(:energia, 50)
      espadachin.set_property(:habilidad, 0.5)
      espadachin.set_property(:potencial_espada, 30)
      espadachin.set_prototype(guerrero)

      espadachin.set_method(:potencial_ofensivo, proc { ||
        self.potencial_espada * self.habilidad
      })

      espadachin.atacar_a(guerrero)
      expect(guerrero.energia).to eq(95)
    end

    # it "multiples prototipos" do
    #   guerrero = PrototypedObject.new
    #   alquimista = PrototypedObject.new
    #
    #   guerrero.set_method(:hola, proc { "HOLA" })
    #   alquimista.set_method(:hola, proc {"se te apetece alguna de mis pociones" })
    #
    #   coso = PrototypedObject.new
    #   coso.set_prototypes([guerrero, alquimista])
    #
    #   expect(coso.hola).to eq "HOLA"
    # end
  end
end