require 'rspec'
require_relative '../src/multimethods'

describe 'Partial Block' do
  describe 'matches' do
    it 'matchea cuando cada parametro se corresponde a un tipo' do
      expect(PartialBlock.new([String]).matches?('parametro')).to be true
    end

    it 'no matchea cuando hay mas parametros que tipos' do
      expect(PartialBlock.new([String]).matches?('parametro', 42)).to be false
    end

    it 'no matchea cuando hay menos parametros que tipos' do
      expect(PartialBlock.new([String, Fixnum]).matches?('parametro')).to be false
    end

    it 'no matchea cuando algun parametro no se corresponde con algun tipo' do
      expect(PartialBlock.new([String, String]).matches?('parametro', 42)).to be false
    end

    it 'matchea cuando los parametros se corresponden con algun subtipo de los tipos' do
      expect(PartialBlock.new([Object]).matches?('parametro')).to be true
    end
  end

  describe 'call' do
    it 'raisea argument error cuando no matchean los parametros' do
      expect { PartialBlock.new([String, Fixnum]).call('parametro') }.to raise_error ArgumentError
    end

    it 'llama al bloque cuando matchean los parametros' do
      expect((PartialBlock.new([String]) { 42 }).call('parametro')).to eq 42
    end
  end
end

describe 'Multimethods' do
  class A
    partial_def :concat, [String, String] do |s1, s2|
      s1 + s2
    end

    partial_def :concat, [String, Integer]  do |s1, n|
      s1 * n
    end

    partial_def :concat, [Object, Object] do |o1, o2|
      'Objetos Concatenados'
    end

    partial_def :concat, [String] do |s1|
      my_name + s1
    end

    def my_name
      'A'
    end
  end

  it 'si existe algun partial block que matchee para los parametros se usa ese partial block' do
    expect(A.new.concat('hello', ' world')).to eq('hello world')
    expect(A.new.concat('hello', 3)).to eq('hellohellohello')
  end

  it 'si no existe ningún partial block que matchee dados los parametros explota con no method error' do
    expect { A.new.concat(['hello', ' world', '!']) }.to raise_error(NoMethodError, 'No existe un multimethod para este metodo')
  end

  it 'un objeto con multimethod deberia saber responder al metodo asociado a ese multimethod' do
    expect(A.new.respond_to?(:concat)).to eq true
  end

  it 'un objeto con multimethod deberia saber responder al metodo asociado a ese multimethod dados ciertos tipos' do
    expect(A.new.respond_to?(:concat, false, [String, String])).to eq true
  end

  it 'un objeto con multimethod no deberia saber responder al metodo asociado a ese multimethod dados ciertos tipos que no coinciden a los de su multimethod' do
    expect(A.new.respond_to?(:concat, false, [String, BasicObject])).to eq false
  end

  it 'deberia ejecutarse en el contexto del objeto' do
    expect(A.new.concat('sd')).to eq 'Asd'
  end

  it 'deberia permitir agregar multimethods una vez que la clase ya fue creada' do
    class B
      partial_def :+, [String] do |n| n + 'B' end
    end

    class B
      partial_def :+, [Float] do |n| 42 end
    end

    expect(B.new + 'asd').to eq 'asdB'
    expect(B.new + 3.2).to eq 42
  end

  context 'multimethods con tipado estructural' do
    class Pepita
      attr_accessor :energia

      def initialize
        @energia = 0
      end

      partial_def :interactuar_con, [[:ser_comida_por]] do |comida|
        comida.ser_comida_por(self)
      end

      partial_def :interactuar_con, [[:entrenar, :alimentar]] do |entrenador|
        entrenador.entrenar(entrenador.alimentar(self))
      end
    end

    class Comida; def ser_comida_por(comensal); comensal.energia += 30; comensal; end ; end

    class Entrenador
      def alimentar(golondrina); golondrina.interactuar_con(Comida.new); golondrina end
      def entrenar(golondrina); golondrina.energia -= 10; golondrina end
    end
  end

  it 'un objeto deberia poder responder que sabe contestar metodos con tipado estructural' do
    expect(Pepita.new.respond_to?(:interactuar_con, false, [Comida])).to eq true
    expect(Pepita.new.respond_to?(:interactuar_con, false, [Entrenador])).to eq true
    expect(Pepita.new.respond_to?(:interactuar_con, false, [Fixnum])).to eq false
  end

  it 'un objeto deberia contestar con el multimethod correspondiente' do
    expect(Pepita.new.interactuar_con(Comida.new).energia).to eq 30
    expect(Pepita.new.interactuar_con(Entrenador.new).energia).to eq 20
  end
end