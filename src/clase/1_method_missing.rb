class Object
  def try(message)
    if respond_to?(message)
      send(message)
    end
  end
end

class Sordo
  private def method_missing(name, *args)
    puts "EH?"
    self
  end
end

class Guerrero
  attr_reader :salud
  def initialize
    @salud = 100
  end
  def descansar
    @salud += 10
  end

  def recibir_danio(danio)
    @salud -= danio
  end

  def pedir_uber
    raise NoMethodError.new()
  end

  private def method_missing(name, *args)
    if name.to_s.start_with?("come_")
      # @salud += name.to_s.delete_prefix("come_").size
      self.class.define_method(name) do
        @salud += name.to_s.delete_prefix("come_").size
      end
      send(name, *args)
    else
      super
    end
  end

  def respond_to_missing?(name, include_private = false)
    name.to_s.start_with?("come_") || super
  end
end

class Espadachin < Guerrero

end

# atila = Guerrero.new
# atila.descansar
# atila.comer_pollo

# puts atila.salud
