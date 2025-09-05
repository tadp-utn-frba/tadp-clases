# frozen_string_literal: true
class Persona
  attr_accessor :nombre, :edad

  def initialize(nombre, edad)
    @nombre = nombre
    @edad = edad
  end
end

pepe = Persona.new("Pepe", 19)
atila = Persona.new("Atila", 40)

# p1 = proc do |persona|
#   puts "#{persona.nombre} -> #{persona.edad}"
# end
#
# p2 = proc do
#   puts "#{nombre} -> #{edad}"
# end
#
# p1.call(pepe)
# p1.call(atila)
#
# pepe.instance_exec(&p2)
# atila.instance_exec(&p2)
