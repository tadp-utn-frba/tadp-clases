# puts "hola"
#self.puts "hola" (es lo mismo)

# Como hago ese codigo pero que no se ejecute? Proc
# Proc es un objeto que encapsula un bloque de código, que puede ser llamado con el método call.

imprimir_hola = Proc.new { puts "Hola" } # Si no lo llamo no se ejecuta
# imprimir_hola.call
# imprimir_hola.call
# imprimir_hola.call
# imprimir_hola.call # puedo ejecutarlo la cantidad de veces que quiera

[1,2,3,4,5].each { |numero| puts numero } # es lo mismo que el de arriba
# Pero a esto le falta el proc
# Tengo la posibilidad de que reciba un bloque por parametro
# Todos los metodos en ruby reciben un bloque implicito

def m1(un_bloque)

end

m1 do
  puts "Hola" # este bloque nunca se esta ejecutando
end

# Fijarse que asi falla

def m2
  yield
  # yield, de esta forma lo ejecuto pero puedo querer agarrar el bloque y ejecutarlo en otro momento. Eso lo hago con el &bloque, tengo una referencia al bloque
  # La desventaja de yield es que solo lo puedo usar dentro de este mensaje, no se lo puedo pasar a nada
end

m2 do
  puts "Hola"
end

def m3(&bloque)
  bloque.call
end

m3 do
  puts "Chau"
end

# LOS BLOQUES NO SON OBJETOS, SON UNA FORMA DE PASAR CODIGO A UN METODO