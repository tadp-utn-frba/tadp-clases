require_relative "../src/tadspec"
require_relative "./persona"

describe "assertions" do #rspec

  before do
    class << self
      include Matchers #tadspec
    end
  end

  it "un string deberia poder assertarse con ser_igual" do #rspec
    juan = Persona.new("juan", 20)
    juan.nombre.deberia ser igual "juan" #tadspec
    juan.saludar("pablo")
        .deberia ser igual "Hola pablo, soy juan"
  end

  it "un string deberia poder assertarse con ser_igual y fallar" do #rspec
    expect{ #rspec
      "pepe".deberia ser igual "luis" #tadspec
    }.to raise_error(TadspecAssertionError) #rspec
  end

  it "un number deberia poder assertarse con ser_menor_a y fallar" do #rspec
    expect{ #rspec
      32.deberia ser menor_a 31 #tadspec
    }.to raise_error(TadspecAssertionError) #rspec
  end

  it "un objeto deberia poder ser igual a otro implicitamente" do #rspec
    "pepita".deberia ser "pepita"
  end

  it "un objeto deberia poder ser igual a otro implicitamente" do #rspec
    33.deberia ser mayor_a 32
  end

  it "un objeto deberia poder ser igual a otro implicitamente" do #rspec
    2.deberia ser uno_de_estos [1,2,3]
  end

  it "deberia poder usar assertions dinamicas" do #rspec
    pablo = Persona.new("Pablo", 30)
    pablo.deberia ser_viejo
  end

  it "deberia poder usar assertions dinamicas en numeros" do #rspec
    32.deberia ser_even
  end

  it "deberia poder negar matchers" do #rspec
    32.deberia no ser_odd
  end

  it "deberia poder usar assertions dinamicas en strings" do #rspec
    matcher = ser_empty
    "".deberia matcher
    [].deberia matcher
  end
end

describe("matchers") do
  it "deberia definir respond to missing" do
    obj = Object.new
    class << obj
      include Matchers
    end

    expect(obj.respond_to? :ser_viejo).to be(true)
    expect(obj.respond_to? :estar_viejo).to be(false)
  end
end