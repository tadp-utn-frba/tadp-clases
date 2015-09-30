require 'rspec'
require_relative '../src/multimethods'

describe 'Multimethods' do
  it 'primer punto' do
    helloBlock = PartialBlock.new([String]) do |who|
      "Hello #{who}"
    end

    expect(helloBlock.matches "a").to eq(true)  #true
    expect(helloBlock.matches 1).to eq(false) #false
    expect(helloBlock.matches "a", "b").to eq(false) #false
  end

  it 'multimethods' do
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
    end

    expect(A.new.concat('hello', ' world')).to eq('hello world')
    expect(A.new.concat('hello', 3)).to eq('hellohellohello')
    expect{A.new.concat(['hello', ' world','!'])}.to raise_error(NoMethodError)

    # A.multimethods().to eq([:concat])
    # A.multimethods(:concat)
    #
    # expect(A.new.concat(Object.new, 3)).to eq('Objetos Concatenados')
    #

  end

  it 'multimethods 2' do
   # class Soldado
   #
   # end
   #
   #  class Tanque
   #
   #  end
  end

  it 'respond_to' do
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
    end


    (A.new.respond_to? :concat).to eq(true)
    (A.new.respond_to? :is_a?).to eq(true)
    (A.new.respond_to? :concat, false, [String, String]).to eq(true)
    (A.new.respond_to? :concat, false, [String, A]).to eq(true)
    (A.new.respond_to? :concat, false, [String]).to eq(false)
    (A.new.respond_to? :concat, false, [String, String, String]).to eq(false)


  end

  it 'Implemento duck typing' do

    class B
      partial_def :concat, [String, [:m, :n], Integer] do |o1, o2, o3|
        'Objetos Concatenados'
      end
    end
  end


end