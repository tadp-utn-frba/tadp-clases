require_relative "./persona"
require_relative "../src/tadspec"

class PersonaHome
  def todas_las_personas
    []
  end

  def personas_viejas
    self.todas_las_personas.select{|p| p.viejo?}
  end
end


describe("mocks") do
  before(:each) do
    class << self
      include Matchers #tadspec
    end
    @persona_home = PersonaHome.new
  end

  after(:each) do #rspec
    PersonaHome.clear_mocks

    expect(@persona_home.todas_las_personas).to eq([])
  end

  it "deberia permitir mockear un metodo de una clase" do
    nico = Persona.new("nico", 30)
    axel = Persona.new("axel", 30)
    lean = Persona.new("lean", 22)

    # Mockeo el mensaje para no consumir el servicio y simplificar el test
    PersonaHome.mockear(:todas_las_personas) do
      [nico, axel, lean]
    end

    viejos = @persona_home.personas_viejas

    viejos.deberia ser [nico, axel]
  end
end