class PartialBlock
  attr_accessor :tipos, :bloque
  def initialize(tipos, &block)
    raise "El bloque debería tener la misma aridad que la lista de tipos" unless block.arity() == tipos.length()

    self.tipos = tipos
    self.bloque = block
  end

  def matches?(*args)
    args.length == tipos.length &&
        tipos.zip(args).all? {|tipoYValor|
          tipo, valor = tipoYValor
          if(tipo.is_a? Array)
            tipo.all? {|symbol| valor.respond_to? symbol}
          else
            valor.is_a? tipo
          end
        }
  end
  def matches_types?(actual_types)
    actual_types.length == tipos.length &&
        tipos.zip(actual_types).all? {|tipoOriginalYActual|
          tipo, tipo_actual = tipoOriginalYActual
          tipo_actual.ancestors.include? tipo
        }
  end
  def call(*args)
    validate_args(*args)
    bloque.call(*args)
  end
  def call_with_context(contexto, *args)
    validate_args(*args)
    contexto.instance_exec(*args, &bloque)
  end
  def validate_args(*args)
    raise "El bloque no coincide con los argumentos" unless matches?(*args)
  end

  def distance_to(*args)
    (1 .. args.size).zip(args.zip(tipos))
        .sum {|index, argAndType|
          arg, type = argAndType
          param_distance(arg, type) * index
        }
  end

  private
  def param_distance(parameter, type)
    if(type.is_a? Array)
      0.5
    else
      parameter.class.ancestors.index(type)
    end
  end

end

class Module
  def partial_def(selector, tipos, &bloque)
    # guardar la info de esta definición parcial
    multimethod = self.has_multimethod(selector) ? multimethod(selector) : self.define_multimethod(selector)
    multimethod.add_definition(tipos, &bloque)
    # asegurar que entiendan ese selector y reaccionen como corresponde
    define_method(selector) do |*args|
      # self es la instancia de la clase que tiene el multimethod
      multimethod.call(self, *args)
    end
  end

  def define_multimethod(selector)
    multimethod_nuevo = Multimethod.new(selector)
    self.all_multimethods << multimethod_nuevo
    multimethod_nuevo
  end
  def has_multimethod(selector)
    self.all_multimethods.any? {|multimethod|
      multimethod.selector == selector
    }
  end
  def multimethod(selector)
    self.all_multimethods.find {|multimethod|
      multimethod.selector == selector
    }
  end
  def multimethods
    self.all_multimethods.collect {|multimethod|
      multimethod.selector
    }
  end

  def all_multimethods
    @all_multimethods ||= []
    @all_multimethods
  end
end

class Multimethod
  attr_accessor :selector, :definiciones
  def initialize(selector)
    self.selector = selector
    self.definiciones = []
  end
  def add_definition(tipos, &bloque)
    definiciones << PartialBlock.new(tipos, &bloque)
  end
  def call(contexto, *args)
    # buscar la definición
    partialBlock = definiciones
                       .select {|definicion| definicion.matches?(*args)}
                       .min_by {|definicion| definicion.distance_to(*args)}
    raise "Ninguna definición aplica para los argumentos" if partialBlock.nil?
    # ejecutarla
    partialBlock.call_with_context(contexto, *args)
  end
  def is_defined(tipos)
    definiciones.any? {|definicion|
      definicion.matches_types?(tipos)
    }
  end
end

class Object
  alias_method :__respond_to? , :respond_to?
  def respond_to?(selector, bool=false, tipos = nil)
    if tipos.nil?
      self.__respond_to?(selector, bool)
    else
      self.class.has_multimethod(selector) &&
          self.class.multimethod(selector).is_defined(tipos)
    end
  end
end