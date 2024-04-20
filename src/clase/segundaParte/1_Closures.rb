# puts "hola"
#self.puts "hola" (es lo mismo)

# Como hago ese codigo pero que no se ejecute? Proc
# Proc es un objeto que encapsula un bloque de código, que puede ser llamado con el método call.

imprimir_hola = Proc.new { puts "Hola" } # Si no lo llamo no se ejecuta


