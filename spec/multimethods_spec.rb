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

  # 5
  # helloBlock.matches?("a") #true
  # helloBlock.matches?(1) #false
  # helloBlock.matches?("a", "b") #false
end