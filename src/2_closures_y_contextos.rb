# self.puts("hola")
# self.puts(self)
# "hola".send(:puts, self)

# imprimir = proc { puts self }
# puts() { puts self }
# [1,2,3].each { |n| puts n }
# def m1(proc, otro_proc)
#   proc.call + otro_proc.call
# end

# m1(proc { 2 + 6 } , proc { 10 + 7 })

# otro_proc = proc { 5 + 11 }
# puts otro_proc.class
# puts Proc.instance_methods(false)
# mi_proc = Proc.new { 5 + 11 }

# def m2(&un_bloque)
#   un_bloque.call
# end
# imprimir_3 = proc { puts "3" }
# m2(&imprimir_3)
# imprimir.call
# imprimir.call
# imprimir.call
# imprimir.call


# [1, 2, 3].each(imprimir)
# [1, 2, 3].each { |n| puts n }

# def contador
#   n = 0
#   proc do
#     n += 1
#     n
#   end
# end
# c1 = contador
# puts c1.call
# puts c1.call
# c2 = contador
# puts c2.call

nombre = "pepita"
# saludo = "..."
# if true
#   saludo = "lalala"
# end
# puts saludo.inspect
# saludo = "..."

este = self
saludar = proc do
  este_otro = self
  puts "Hola " + nombre
end
saludar.call





class A
  attr_accessor :nombre

  def saludar
    self
    puts "Hola " + nombre
  end
end
saludar2_proc = proc do
  puts "Soy #{self}"
  puts "Hola " + nombre
end
A.define_method(:saludar2, &saludar2_proc)
saludar2_proc.call
A.new.saludar2

B = Class.new do
  nombre = "Axel"
  puts nombre
  puts self

  def m1
  end
end
puts nombre

class Guerrero
  def energia
    10
  end
  def ejecutar_proc(un_proc)
    un_proc.call
  end
end
atila = Guerrero.new
atila.instance_eval(&saludar2_proc)
devolver_energia = proc do
  energia
end
class Golondrina
  def energia
    "sí"
  end
end
puts Golondrina.new.instance_eval(&devolver_energia)
puts atila.instance_eval(&devolver_energia)
