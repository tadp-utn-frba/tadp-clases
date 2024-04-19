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

def m1

end

m1 do
  puts "Hola" # este bloque nunca se esta ejecutando
end