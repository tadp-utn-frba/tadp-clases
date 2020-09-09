require "rspec"
require_relative "../src/multimethods"

describe "Partial Blocks" do
  describe "construcción de partial block" do
    it("deberia poder crearse con una lista de longitud acorde al bloque") do
      expect { PartialBlock.new([String]) do |s| "mucho no importa" end }.not_to raise_error
    end
    it("no deberia poder crearse con una lista de longitud menor a la aridad del bloque") do
      expect { PartialBlock.new([String]) do |s1, s2| "mucho no importa" end }.to raise_error("El bloque no coincide con los tipos")
    end
    it("no deberia poder crearse con una lista de longitud mayor a la aridad del bloque") do
      expect { PartialBlock.new([String, String]) do |s1| "mucho no importa" end }.to raise_error("El bloque no coincide con los tipos")
    end
  end

  describe "matches" do
    it("deberia matchear el mismo tipo") do
        un_bloque = PartialBlock.new([String]) do |s|
          "mucho no importa"
        end

        expect(un_bloque.matches?("unString")).to be(true)
    end

    it("deberia matchear super tipo") do
      un_bloque = PartialBlock.new([Object]) do |s|
        "mucho no importa"
      end

      expect(un_bloque.matches?("unString")).to be(true)
    end

    it("deberia aceptar modulos como tipos para la firma") do
      un_modulo = Module.new
      una_clase = Class.new
      una_clase.include(un_modulo)

      un_bloque = PartialBlock.new([un_modulo]) do |s|
        "mucho no importa"
      end

      expect(un_bloque.matches?(una_clase.new)).to be(true)
    end

    it("deberia no matchear si no hay relación") do
      un_bloque = PartialBlock.new([String]) do |s|
        "mucho no importa"
      end

      expect(un_bloque.matches?(3)).to be(false)
    end
    it("deberia no matchear si no coincide la cantidad de argumentos") do
      un_bloque = PartialBlock.new([String]) do |s|
        "mucho no importa"
      end

      expect(un_bloque.matches?("hola", "mundo")).to be(false)
    end

    it("deberia matchear con multiples parámetros") do
      un_bloque = PartialBlock.new([String, String]) do |s1, s2|
        "mucho no importa"
      end

      expect(un_bloque.matches?("s1", "s2")).to be(true)
      expect(un_bloque.matches?("s1", 1)).to be(false)
      expect(un_bloque.matches?(1, 3)).to be(false)
    end
  end

  describe "call" do
    helloBlock = PartialBlock.new([String]) do |who|
      "Hello #{who}"
    end

    it("deberia ejecutar con el parámetro que le paso") do
      expect(helloBlock.call("Juan")).to eq("Hello Juan")
    end

    it("deberia arrojar error cuando le paso un tipo que no corresponde") do
      expect{helloBlock.call(1)}.to raise_error("El bloque no coincide con los argumentos")
    end

    it("deberia ejecutarse con instancias de subtipos") do
      pairBlock = PartialBlock.new([Object, Object]) do |left, right|
        [left, right]
      end

      expect(pairBlock.call("hello", 1)).to eq(["hello",1])
    end
  end
end

=begin
describe "partial_def" do
  class A
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

  it("deberia funcionar concat") do
    expect(A.new.concat('hello', ' world')).to eq('hello world')
    expect(A.new.concat('hello', 3)).to eq('hellohellohello')
    expect(A.new.concat(['hello', ' world', '!'])).to eq('hello world!')

      #A.new.concat('hello', 'world', '!') # Lanza una excepción!
  end

  class C
    partial_def :blah, [String, String] do |s1,s2|
      s1 + s2
    end
  end

  it 'debería saber si responde a un mensaje' do
    expect(C.new.respond_to? :blah).to be(true)
  end

  it 'debería saber si responde a un mensaje con tipos' do
    expect(C.new.respond_to? :blah, false, [String, String]).to be(true)
  end

  it 'debería saber si no responde a un mensaje con tipos' do
    expect(C.new.respond_to? :blah, false, [Integer, Integer]).to be(false)
  end

  class Soldado
# ... implementación de soldado
  end

  class Tanque
    def ataca_con_canion(objetivo)
      "canion"
    end

    def ataca_con_ametralladora(objetivo)
      "ametralladora"
    end

    partial_def :ataca_a, [Tanque] do |objetivo|
      self.ataca_con_canion(objetivo)
    end

    partial_def :ataca_a, [Soldado] do |objetivo|
      self.ataca_con_ametralladora(objetivo)
    end
  end

  it "deberia funcionar con self" do
    tanque = Tanque.new
    soldado = Soldado.new

    expect(tanque.ataca_a(soldado)).to eq("ametralladora")
    expect(tanque.ataca_a(tanque)).to eq("canion")
  end

  class F
    partial_def :formatear, [String, [:nombre, :direccion]] do |titulo, coso|
      titulo + " | " + coso.nombre + ": " + coso.direccion
    end
    partial_def :formatear, [String, [:peso]] do |titulo, pesable|
      titulo + " " + pesable.peso.to_s
    end
  end

  class Lugar
    attr_accessor :nombre, :direccion, :fotos
    def initialize(nombre, direccion)
      @nombre = nombre
      @direccion = direccion
    end
  end

  class Perro
    attr_accessor :nombre, :edad, :peso
    def initialize(peso = 20, nombre = "pepe", edad = 10)
      @nombre = nombre
      @edad = edad
      @peso = peso
    end
  end

  it "deberia soportar ducktyping" do
    expect(
        F.new.formatear("VISITE", Lugar.new("Obelisco", "Corrientes y 9 de Julio"))
    ).to eq("VISITE | Obelisco: Corrientes y 9 de Julio")

    expect(
        F.new.formatear("Pesado", Perro.new(32))
    ).to eq("Pesado 32")
  end
end

=end