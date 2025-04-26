class Coso
  def initialize(una_y)
    @y = una_y
  end
  def y
    @y
  end
end

mi_proc = proc do
  x = 10
  puts x + y
end
Coso.new(30).instance_exec do
  x = 10
  puts x + y
end
Coso.new(10).instance_exec do
  x = 10
  puts x + y
end
sumar_x_mas_y = proc do
  x = 10
  puts x + y
end
Coso.new(20).instance_exec(&sumar_x_mas_y)
Coso.new(10).instance_exec(&sumar_x_mas_y)