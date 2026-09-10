puts "hola"
# es lo mismo que
self.puts "hola"

imprimir_hola = proc { puts "hola" }
imprimir_hola # no se imprime nada
imprimir_hola.call # hay que mandarle call para que evalue

# podemos tambien hacer:
[1, 2, 3].each { |n| puts n }

# ¿pero podemos hacer?
# [1, 2, 3].each(imprimir_hola)

# ¿cual es la diferencia?:
# ¿como hacemos un método que tome {} o do end?:

# m { puts "chau" }

# no funciona
# def m(un_bloque)
#   un_bloque.call
# end
# si estuviese definido asi, necesitariamos usarlo como
# m(proc { puts "chau" })

# podemos escribir:

# def m
#   yield
# end
# o
# def m(&un_bloque)
#   un_bloque.call
# end

#############

# ¿puede un bloque acceder a variables de afuera?

nombre = "pepe"
saludar = proc { puts "hola " + nombre }
saludar.call

# ¿que pasa si cambio el valor?
nombre = "josefa"
saludar.call

# ¿...se podra reasignar el nombre desde adentro de un bloque?
renombrar = proc { |nuevo_nombre| nombre = nuevo_nombre }
renombrar.call("axel")
saludar.call

# ¿y podre crear variables nuevas desde dentro de un bloque?
agregar_hola = proc { hola = "HOLA" }
agregar_hola.call
# puts hola

# volviendo a usar una variable de afuera, ¿puedo hacer eso definiendo un metodo?:

def saludar
  puts "Hola " + nombre
end
saludar  # ¿Qué es saludar acá? ¿self.saludar o la variable local saludar?
# def _saludar
#   puts "Hola " + nombre
# end
# _saludar

# ¿y si usamos define_method?
define_method(:_saludar) do
  puts "Hola " + nombre
end
_saludar

# de igual manera, class nos corta el contexto con uno nuevo:
# class A
#   puts nombre
# end
# pero si lo definimos asi:
B = Class.new do
  puts nombre
end
