require 'rspec'
require_relative '../src/multimethods'

RSpec.describe 'Multimethods' do
  it 'crear un PartialBlock falla si tiene distinta cantidad de parametros en su bloque que en su lista de tipos' do
    expect do
      PartialBlock.new([]) { |who| "Hello #{who}" }
    end.to raise_error(ArgumentError)
  end

  it '....' do
    hello_block = PartialBlock.new([String]) do |who|
      "Hello #{who}"
    end

    expect(hello_block.matches?("a")).to be true
    expect(hello_block.matches?(1)).to be false
    expect(hello_block.matches?("a", "b")).to be false
  end

  it "...call" do
    hello_block = PartialBlock.new([String]) do |who|
      "Hello #{who}"
    end

    expect(hello_block.call("world!")).to eq("Hello world!")
    expect { hello_block.call(1) }.to raise_error(ArgumentError)

    pairBlock = PartialBlock.new([Object, Object]) do |left, right|
      [left, right]
    end

    expect(pairBlock.call("hello", 1)).to eq ["hello", 1]
  end


  context 'multimethods' do
    it 'con un solo partial def' do
      klass = Class.new do
        partial_def :concat, [String, String] do |s1,s2|
          s1 + s2
        end
      end

      expect(klass.new.concat("a", "b")).to eq("ab")
      expect { klass.new.concat("a", 1) }.to raise_error(NoMethodError)
    end

    let(:klass_a) do
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

    it "xx" do
      expect(klass_a.new.concat('hello', ' world')). to eq('hello world')
      expect(klass_a.new.concat('hello', 3)). to eq('hellohellohello')
      expect(klass_a.new.concat(['hello', ' world', '!'])). to eq('hello world!')
      expect { klass_a.new.concat('hello', 'world', '!') }. to raise_error(NoMethodError)
    end

    it "deberia ejecutarse en el contexto del receptor del mensaje" do
      klass = Class.new do
        attr_reader :energia
        def initialize()
          @energia = 0
        end

        partial_def :comer, [Integer] do |gramos|
          @energia += gramos
        end

        partial_def :comer, [Array] do |comidas|
          comidas.each { |gramos| comer(gramos) }
        end
      end

      pepita = klass.new
      pepita.comer(2)
      expect(pepita.energia).to eq 2

      josefa = klass.new
      josefa.comer([2, 3, 4, 5])
      expect(josefa.energia).to eq 14
    end
  end
end
