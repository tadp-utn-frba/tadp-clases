# self es siempre el contexto implicito que tenemos en ruby, por eso en una clase no es necesario poner self antes de agarrar una variable de la clase

class Usuario
  attr_accessor :edad

  def initialize(edad)
    @edad = edad
  end

  def edad_de
    proc { edad }
  end

  def con_bloque(bloque)
    bloque.call
  end
end

mayor = Usuario.new(19)
menor = Usuario.new(15)

mayor.edad_de.call(menor) # 15

mayor.instance_eval(&bloque) # 19. Ejecutar en el contexto de otro objeto