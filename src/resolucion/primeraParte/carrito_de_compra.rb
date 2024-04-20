class CarritoDeCompra
  attr_accessor :productos

  def agregar_producto(producto, precio)
    productos << [producto, precio]
  end

  def total_a_pagar()
    acumulador = 0
    productos.each { |p| acumulador = acumulador + p[1] }
    puts acumulador
  end

  def productos
    @productos || @productos = []
  end
end
