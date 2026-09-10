# ¿Cómo podríamos hacer para escribir objetos con esta sintaxis?:
pepe = object do
  def nombre
    "Pepe"
  end

  def saludar
    puts "Hola soy #{nombre}"
  end
end

pepe.saludar

# lo mismo con clases:

Perro = clase do
  def ladrar
    puts "guau"
  end
end

Perro.new.ladrar

