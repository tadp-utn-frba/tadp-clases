require 'rspec'
require_relative '../src/multimethods'

describe 'PartialBlock' do

  it 'reponde matches' do
    helloBlock = PartialBlock.new([String]) do |who|
      "Hello #{who}"
    end

    expect(helloBlock.matches("a")).to be true
    expect(helloBlock.matches(1)).to be false
    expect(helloBlock.matches("a", "b")).to be false
  end

  it 'call o muere' do
    helloBlock = PartialBlock.new([String]) do |who|
      "Hello #{who}"
    end

    expect(helloBlock.call("a")).to eq "Hello a"
    expect { helloBlock.call(1) }.to raise_error(ArgumentError, 'No existe un multimethod para este metodo')
  end

end

describe 'Multimethods' do
  class A
    partial_def :concat, [String, String] do |s1, s2|
      s1 + s2
    end

    partial_def :concat, [String, Integer] do |s1, n|
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

  it 'puedo usar multimethods' do
    expect(A.new.concat('hello', ' world')).to eq('hello world')
    expect(A.new.concat(' world')).to eq('A world')
    expect(A.new.concat('hello', 3)).to eq('hellohellohello')
  end

  it 'si no existe ningún partial block que matchee dados los parametros explota con no method error' do
    expect { A.new.concat(['hello', ' world', '!']) }.to raise_error(NoMethodError, 'No existe un multimethod para este metodo')
  end

  it 'un objeto con multimethod deberia saber responder al metodo asociado a ese multimethod' do
    expect(A.new.respond_to?(:concat)).to eq true
  end

  it 'un objeto con multimethod deberia saber responder al metodo asociado a ese multimethod dados ciertos tipos' do
    expect(
        A.new.respond_to?(:concat, false, [String, String])
    ).to eq true
  end

  it 'un objeto con multimethod no deberia saber responder al metodo asociado a ese multimethod dados ciertos tipos que no coinciden a los de su multimethod' do
    expect(
        A.new.respond_to?(:concat, false, [String, BasicObject])
    ).to eq false
  end
end

describe 'duck typing' do
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

  class Comida
    def ser_comida_por(comensal)
      comensal.energia += 30
      comensal
    end
  end

  class Entrenador
    def alimentar(golondrina)
      golondrina.interactuar_con(Comida.new)
      golondrina
    end

    def entrenar(golondrina)
      golondrina.energia -= 10
      golondrina
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