class Cocinero
  attr_accessor :ingredientes, :empanada

  def initialize
    self.ingredientes = []
  end

  def preparar_empanadas(&receta)
    self.instance_eval(&receta)
  end

  def saltear_cebolla
    self.agregar("cebolla salteada")
  end

  def cocinar_carne
    self.agregar("carne cocida")
  end

  def agregar(ingrediente)
    self.ingredientes << ingrediente
  end

  def rellenar_tapas
    self.empanada = Empanada.new(ingredientes)
  end

  def hornear
    self.empanada.hornear
    self.empanada
  end
end

class Empanada
  attr_accessor :ingredientes, :horneada

  def self.receta
    proc {
      saltear_cebolla
      cocinar_carne
      agregar 'huevo'
      agregar 'aceitunas'
      rellenar_tapas
      hornear
    }
  end

  def self.tucumana
    proc {
      saltear_cebolla
      cocinar_carne
      agregar 'huevo'
      agregar 'aceitunas'
      rellenar_tapas
      agregar_papa
      hornear
    }
  end

  def initialize(ingredientes)
    self.ingredientes = ingredientes
  end

  def hornear
    self.horneada = true
  end
end

maiu = Cocinero.new
maiu.preparar_empanadas(&Empanada.receta)

axel = Cocinero.new
axel.define_singleton_method(:agregar) do |ingrediente|
  if ingrediente != "aceitunas"
    super(ingrediente)
  end
end
axel.preparar_empanadas(&Empanada.receta)

# tucumanas
maiu.preparar_empanadas(&Empanada.tucumana) # esto rompe por la papa

class Cocinero
  def method_missing(nombre, *args, &bloque)
    if nombre.to_s.start_with?("agregar_")
      ingrediente = nombre.slice("agregar_".size, nombre.size)
      self.agregar(ingrediente)
    else
      super
    end
  end

  def self.respond_to_missing?(sym, priv = false)
    sym.to_s.start_with?('agregar_')
  end
end

maiu.preparar_empanadas(&Empanada.tucumana) # ahora si


