nombre = "Pepe"

imprimir_hola = proc { puts "Hola #{nombre}" }
imprimir_hola.call
# Puedo acceder a la variable de afuera del bloque

imprimir_hola_modificando = proc do
  nombre = "Juan"
  hola = 2
end

imprimir_hola_modificando.call
puts nombre
# Puedo modificar la variable de afuera del bloque, pero no puedo acceder variables del proc
# Podemos decir entonces que el proc tiene acceso a las variables donde fue definido pero el proc define su propio contexto

# Pero que pasa con los metodos?
