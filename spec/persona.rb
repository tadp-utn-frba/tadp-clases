class Persona
  attr_accessor :nombre, :edad

  def initialize(nombre, edad)
    self.nombre = nombre
    self.edad = edad
  end

  def viejo?
    self.edad > 29
  end

  def saludar(a_alguien)
    "Hola #{a_alguien}, soy #{self.nombre}"
  end
end