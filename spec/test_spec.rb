require_relative '../lib/prototype'

describe 'Prototipos' do
  describe 'Un prototipo' do
    it 'deberia poder agregar una property' do
      guerrero = PrototypedObject.new
      guerrero.set_property(:sabiduria, 50)

      expect(guerrero.get_property(:sabiduria)).to eq 50
      expect { PrototypedObject.new.get_property(:sabiduria) }
        .to raise_error PropertyNotFound
    end

    it 'deberia poder acceder a una property sin usar get_property' do
      guerrero = PrototypedObject.new
      guerrero.set_property(:sabiduria, 50)

      expect(guerrero.sabiduria).to eq 50
      expect { PrototypedObject.new.sabiduria }.to raise_error NoMethodError
    end

    it 'deberia poder saber qué mensajes sabe responder' do
      guerrero = PrototypedObject.new
      guerrero.set_property(:sabiduria, 50)

      expect(guerrero.respond_to?(:sabiduria)).to be true
      expect(guerrero.respond_to?(:insultar)).to be false
    end

    it 'deberia agregar un metodo' do
      guerrero = PrototypedObject.new
      guerrero.set_property(:sabiduria, 50)

      espadachin = PrototypedObject.new
      espadachin.set_property(:sabiduria, 0)

      guerrero.set_property(:enseniar, proc {
        |otro_guerrero| otro_guerrero.set_property(:sabiduria, self.sabiduria + otro_guerrero.sabiduria)
      })

      guerrero.enseniar(espadachin)
      expect(espadachin.sabiduria).to eq 50
    end

    it 'deberia poder crear prototipos' do
      guerrero = PrototypedObject.new
      guerrero.set_property(:sabiduria, 50)

      espadachin = guerrero.copy

      espadachin.set_property(:poder_espada, 100)

      expect(espadachin.respond_to?(:poder_espada)).to be true
      expect(espadachin.poder_espada).to eq 100
      expect(guerrero.respond_to?(:poder_espada)).to be false

      expect(espadachin.respond_to? :sabiduria).to be true

      expect(espadachin.respond_to?(:no_method)).to be false
    end

    it 'deberia actualizar una property sin usar set_property' do
      guerrero = PrototypedObject.new
      guerrero.set_property(:sabiduria, 50)

      guerrero.sabiduria = 30

      espadachin = guerrero.copy

      guerrero.set_property(:enseniar, proc {
        |otro_guerrero| otro_guerrero.sabiduria = self.sabiduria + otro_guerrero.sabiduria
      })

      guerrero.enseniar(espadachin)

      expect(guerrero.sabiduria).to eq 30
      expect(espadachin.sabiduria). to eq 60
    end
  end
end