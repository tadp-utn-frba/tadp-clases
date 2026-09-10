require_relative 'age'

def hacer_combatir(guerrero)
  oponente = Guerrero.new(20, 100, 20)
  guerrero.atacar(oponente)
  oponente.atacar(guerrero)
  guerrero.energia
end
#
# class Guerrero
#   private def method_missing(name, *args)
#     if name.start_with?("comerse_")
#       @energia += name.to_s.delete_prefix("comerse_").size
#     else
#       super
#     end
#   end
#
#   def respond_to_missing?(name, include_private)
#     name.start_with?("comerse_") || super
#   end
# end
#
# atila = Guerrero.new(10, 10, 10)
# puts atila.energia
# puts atila.comerse_un_sanguche
# puts atila.energia
# puts atila.respond_to?(:comerse_un_sanguche)
# puts atila.respond_to?(:descansar)
# puts atila.sarasa.inspect

# ¿Cómo podríamos aprovechar method_missing para hacer un registrador de mensajes recibidos?

class DeafObject < BasicObject
  def method_missing(name, *args)
    self
  end
  def respond_to_missing?(name, include_private)
    true
  end
end

class RegistradorDeMensajes < BasicObject
  attr_reader :mensajes_recibidos
  def initialize(objeto)
    @objeto = objeto
    @mensajes_recibidos = []
  end
  def method_missing(method, *args)
    @mensajes_recibidos << { mensaje: method, parametros: args }
    @objeto.send(method, *args)
  end
  def respond_to_missing?(method, include_private = false)
    @objeto.respond_to?(method, include_private)
  end
end

atila = Guerrero.new
registrador_de_mensajes = RegistradorDeMensajes.new(atila)
puts atila.energia
hacer_combatir(registrador_de_mensajes)
puts atila.energia
puts registrador_de_mensajes.mensajes_recibidos.inspect
puts registrador_de_mensajes.is_a? Guerrero
puts atila.is_a? Guerrero
puts registrador_de_mensajes.to_s
puts atila.to_s
