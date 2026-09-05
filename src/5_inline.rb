# ¿Cómo podríamos hacer para escribir objetos con esta sintaxis?:

def object(&bloque)
  objeto = Object.new
  objeto.singleton_class.class_eval(&bloque)
  objeto
end

pepita = object do
  attr_writer :energia

  def energia
    @energia ||= 0
  end

  def volar(kms)
    self.energia -= kms * 10
  end

  def comer(gramos_de_alpiste)
    self.energia += gramos_de_alpiste
  end
end

puts pepita.energia
pepita.volar(100)
puts pepita.energia

# lo mismo con clases:
# def clase(&bloque)
#   una_clase = Class.new
#   una_clase.class_eval(&bloque)
#   una_clase
# end
Perro = clase do
  def ladrar
    puts "guau"
  end
end

Perro.new.ladrar

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

