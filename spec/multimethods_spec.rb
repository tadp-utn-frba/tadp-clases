require 'rspec'
require_relative '../src/multimethods'

describe "Partial Blocks" do
  let(:helloBlock) do

    PartialBlock.new([String]) do |who|
      "Hello #{who}"
    end

  end

  it "un partial block matchea con un parametro del tipo correcto" do
    expect(helloBlock.matches?("a")).to be true
  end

  it "un partial block no matchea con un parametro del tipo incorrecto" do
    expect(helloBlock.matches?(1)).to be false
  end

  it "un partial block no matchea si la cantidad de parametros no corresponde con la cantidad de tipos" do
    expect(helloBlock.matches?("a", "b")).to be false
  end

  it "un partial block de multiples parametros matchea si cada parametro se corresponde con su tipo" do
    mi_partial_block = PartialBlock.new([String, Integer]) do |who, howMany|
      "Hello #{who}" * howMany
    end

    expect(mi_partial_block.matches?("a", 3)).to be true
  end

  it "un partial block de multiples parametros NO matchea si algun parametro no se corresponde con su tipo" do
    mi_partial_block = PartialBlock.new([String, Integer]) do |who, howMany|
      "Hello #{who}" * howMany
    end

    expect(mi_partial_block.matches?("a", "b")).to be false
  end

  it "el constructor tiene mas elementos que argumentos tiene el bloque proporcionado, falla" do
    expect do
      PartialBlock.new([String]) do |who, what|
        "Hello #{who}"
      end
    end.to raise_error(ArgumentError)
  end

  it "si lo evaluo con unos parametros, deberia darme el resultado del bloque" do
    resultado = helloBlock.call("cualquier string")

    expect(resultado).to eq("Hello cualquier string")
  end

  it "si lo evaluo con parametros invalidos, deberia fallar con ArgumentError" do
    expect {
      helloBlock.call(5)
    }.to raise_error(ArgumentError)
  end

  it "el tipado de un partial block considera subtipos" do
    pairBlock = PartialBlock.new([Object, Object]) do |left, right|
      [left, right]
    end

    expect(pairBlock.call("hello", 1)).to eq(["hello", 1])
  end

  it "el tipado de un partial block considera modules" do
    headBlock = PartialBlock.new([Enumerable]) do |enumerable|
      enumerable.to_a[0]
    end

    expect(headBlock.call([1,2,3])).to eq(1)
    expect(headBlock.call({ a: 1,
                            b: "hundiste mi acarozado" })).to eq([:a, 1])
  end
end

describe "Multimethods" do
  let(:class_a) do
    Class.new do
      partial_def :concat, [String, String] do |s1,s2|
        s1 + s2
      end

      partial_def :concat, [String, Integer] do |s1,n|
        s1 * n
      end

      partial_def :concat, [Array] do |a|
        a.join
      end
    end
  end

  let(:class_b) do
    Class.new do
      partial_def :concat, [Object, Object] do |o1, o2|
        "Objetos concatenados"
      end

      partial_def :concat, [String, Integer] do |s1,n|
        s1 * n
      end
    end
  end

  it ".." do
    un_a = class_a.new
    expect(un_a.concat('hello', ' world'))
        .to eq("hello world")

    expect(un_a.concat('hello', 3)).to eq("hellohellohello")
    expect(un_a.concat(['hello', ' world', '!'])).to eq("hello world!")
    expect { un_a.concat('hello', 'world', '!') }
      .to raise_error(ArgumentError)
  end

  it "un multimethod ejecuta la definicion mas especifica que tenga" do
    un_b = class_b.new

    expect(un_b.concat("Hello", 2)).to eq("HelloHello")
    expect(un_b.concat(Object.new, 3)).to eq("Objetos concatenados")
  end

  it "un objeto deberia poder responder un mensaje que tiene definido como multimethod" do
    un_b = class_b.new

    expect(un_b.respond_to?(:concat)).to be true
  end

  it "un objeto deberia poder responder un mensaje que tiene definido como multimethod si los parametros que le paso son correctos" do
    un_b = class_b.new

    expect(un_b.respond_to?(:concat, false, [String,String])).to be true
  end

  it "..." do
    un_b = class_b.new

    expect(un_b.respond_to?(:concat, false, [Integer,class_b])).to be true
  end

  it "si le paso tipos y lo entendia pero no por multimethods, da false el respond_to?" do
    un_b = class_b.new

    expect(un_b.respond_to?(:to_s, false, [String, String])).to be false
  end

  it "si los tipos no coinciden, da false el respond_to?" do
    un_b = class_b.new

    expect(un_b.respond_to?(:concat, false, [String, String, String])).to be false
  end

  let(:class_con_dos_multimethods) do
    Class.new do
      partial_def :concat, [Object, Object] do |o1, o2|
        "Objetos concatenados"
      end

      partial_def :concat, [String, Integer] do |s1,n|
        s1 * n
      end

      partial_def :saludar, [String] do |x|
        "Hola #{x}"
      end

      partial_def :saludar, [String, Integer] do |x, n|
        "Hola #{x}" + "!" * n
      end
    end
  end

  it "deberiamos poder tener mas de un multimethod" do
    un_objeto_con_dos_multimethods = class_con_dos_multimethods.new

    expect(un_objeto_con_dos_multimethods.concat("a", 3)).to eq("aaa")
    expect(un_objeto_con_dos_multimethods.saludar("juan carlos", 3)).to eq("Hola juan carlos!!!")
  end

  describe "usando self" do
    let(:class_c) do
      Class.new do
        attr_accessor :energia

        def initialize
          @energia = 0
        end

        partial_def :volar, [Integer] do |kms|
          self.energia -= kms
        end

        partial_def :volar, [] do
          self.volar(10)
        end
      end
    end

    it "los multimethods pueden referenciar a self" do
      pepita = class_c.new

      pepita.volar(10)

      expect(pepita.energia).to eq(-10)
    end
  end
end

