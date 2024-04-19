require './carrito_de_compra'

def ir_de_compras_con(un_carrito)
  un_carrito.agregar_producto("leche", 2)
  un_carrito.agregar_producto("harina", 15)
  un_carrito.total_a_pagar
  un_carrito.agregar_producto("harina", 15)
end

class Registrador_de_compras
  attr_reader :mensajes_recibidos

  def initialize(objeto_original)
    @objeto_original = objeto_original
    @mensajes_recibidos = []
  end

  private def method_missing(symbol, *args, &block)
    @mensajes_recibidos << { mensaje: symbol, argumentos: args }
    @objeto_original.send(symbol, *args, &block)
  end
end

carrito = CarritoDeCompra.new
ir_de_compras_con(carrito)

