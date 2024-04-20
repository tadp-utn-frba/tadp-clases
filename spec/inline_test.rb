require_relative '../src/segundaParte/inline/5_inline'

pepe = objeto do
  def nombre
    "Pepe"
  end

  def saludar
    "Hola #{nombre}"
  end
end

puts pepe.saludar == "Hola Pepe"

Persona = clase do
  attr_reader :nombre

  def initialize(nombre)
    @nombre = nombre
  end
end
pepe = Persona.new("Pepe")
puts pepe.nombre == "Pepe"
