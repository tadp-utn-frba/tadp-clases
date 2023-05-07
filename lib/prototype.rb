class PropertyNotFound < Exception
  attr_accessor :propiedad, :objeto

  def initialize(propiedad, objeto)
    self.propiedad = propiedad
    self.objeto = objeto
  end
end
class PrototypedObject
  attr_accessor :propiedades, :prototipo
  def initialize(prototipo = nil )
    self.propiedades = {}
    self.prototipo = prototipo
  end
  def copy()
    PrototypedObject.new(self)
  end
  def set_property(nombre, valor)
    self.propiedades[nombre] = valor
  end
  def get_property(nombre)
    if self.propiedades.has_key? nombre
      self.propiedades[nombre]
    elsif self.prototipo.nil?
      raise PropertyNotFound.new(nombre, self)
    else
      self.prototipo.get_property(nombre)
    end
  end
  def has_property?(symbol)
    self.propiedades.has_key?(symbol) || self.prototipo&.has_property?(symbol)
  end
  def respond_to_missing?(symbol, include_all)
    self.has_property?(symbol.to_s.chomp("=").to_sym) || super
  end
  def method_missing(symbol, *args, **kargs, &block)
    if symbol.to_s.end_with? "="
      set_property(symbol.to_s.chomp("=").to_sym, args[0])
    elsif self.has_property? symbol
      propiedad = self.get_property(symbol)
      if propiedad.is_a? Proc
        instance_exec(*args, **kargs, &propiedad)
      else
        propiedad
      end

    else
      super
    end
  end
end