require_relative 'age'

def hacer_combatir(guerrero)
  oponente = Guerrero.new(20, 100, 20)
  guerrero.atacar(oponente)
  oponente.atacar(guerrero)
  guerrero.descansar
  oponente.descansar
  guerrero.energia
end

class RegistradorDeMensajes < BasicObject
  attr_reader :mensajes_recibidos

  def initialize(objeto_original)
    @objeto_original = objeto_original
    @mensajes_recibidos = []
  end

  private def method_missing(name, *args)
    @mensajes_recibidos << { mensaje: name, parametros: args }
    @objeto_original.send(name, *args)
  end

  def respond_to_missing?(name, include_private)
    @objeto_original.respond_to?(name, include_private)
  end
end

atila = Guerrero.new(20, 100, 10)
registrador_de_mensajes = RegistradorDeMensajes.new(atila)

hacer_combatir(registrador_de_mensajes)

puts registrador_de_mensajes.mensajes_recibidos

# puts atila.respond_to?("atacar")
# puts registrador_de_mensajes.respond_to?("atacar")
# puts atila.is_a? Guerrero
# puts registrador_de_mensajes.is_a? Guerrero
