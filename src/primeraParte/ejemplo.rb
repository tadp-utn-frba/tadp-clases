require './carrito_de_compra'

def ir_de_compras_con(un_carrito)
  un_carrito.agregar_producto("leche", 2)
  un_carrito.agregar_producto("harina", 15)
  un_carrito.total_a_pagar
  un_carrito.agregar_producto("harina", 15)
end

class Registrador_de_compras < BasicObject
  attr_reader :mensajes_recibidos

  def initialize(objeto_original)
    @objeto_original = objeto_original
    @mensajes_recibidos = []
  end

  def method_missing(symbol, *args, &block)
    @mensajes_recibidos << { mensaje: symbol, argumentos: args }
    @objeto_original.send(symbol, *args, &block)
  end

  def respond_to_missing?(symbol, include_private = false)
    @objeto_original.respond_to?(symbol, include_private) # || super
  end
end

carrito = CarritoDeCompra.new
registrador_de_mensajes = Registrador_de_compras.new(carrito)
ir_de_compras_con(registrador_de_mensajes)

puts registrador_de_mensajes.mensajes_recibidos

# Respond_to?

puts carrito.respond_to? :total_a_pagar
puts registrador_de_mensajes.respond_to? :total_a_pagar

# is_a?

puts carrito.is_a? CarritoDeCompra
puts registrador_de_mensajes.is_a? CarritoDeCompra
