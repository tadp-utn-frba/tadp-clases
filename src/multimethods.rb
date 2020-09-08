class PartialBlock
  def initialize(types, &block)
    unless block.arity == types.length
      raise "El bloque debería tener la misma aridad que la lista de tipos"
    end

    @types = types
    @block = block
  end

  def matches?(*args)
    matches_types? args.map {|it| it.class }
      # args.length == @types.length &&
      #     args.zip(@types).all? do |par|
      #       par[0].is_a? par[1]
      #     end
  end

  def call(*args)
    unless matches? *args
      raise "El bloque no coincide con los argumentos"
    end

    @block.call(*args)
  end

  def call_with(instance, *args)
    unless matches? *args
      raise "El bloque no coincide con los argumentos"
    end

    instance.instance_exec(*args, &@block)
  end

  def distance_to(*args)
    args.zip(@types).sum do |par|
      if par[1].is_a? Array
        0.5
      else
        par[0].class.ancestors.index(par[1])
      end
    end
  end

  def matches_types?(types)
    types.length == @types.length &&
        types.zip(@types).all? do |it|
          matches_single_type(it)
        end
  end

  def matches_single_type(par)
    tipo_definido = par[1] #Por ejemplo, Object, String, [:a, :b]
    tipo_recibido = par[0] #Por ejemplo, Integer, Numeric, Object, String
    if tipo_definido.is_a? Array
      tipo_definido.all? do |mensaje|
        ## TODO: mostrar alternativas con instance_methods.include?
        tipo_recibido.method_defined? mensaje
      end
    else
      #Una clase o un modulo
      tipo_recibido <= tipo_definido
    end
  end
end

class Multimethod
  def definiciones
    @definiciones ||= Hash.new
  end

  def agregar_definicion(types, block)
    definiciones[types] = PartialBlock.new(types, &block)
  end

  def call(this, *args)
    partial_block = definiciones.values
        .select { |it| it.matches? *args }
        .min_by { |it| it.distance_to *args}

    return this.method_missing(*args) if partial_block.nil?

    partial_block.call_with(this, *args)
  end

  def matches_types?(types)
    definiciones.values.any? do |it|
      it.matches_types? types
    end
  end
end

class Module

  def partial_def(nombre, types, &block)

    unless multimethods.has_key? nombre
      multimethods[nombre] = Multimethod.new
    end

    multimethods[nombre].agregar_definicion(types, block)

    define_method(nombre) do |*args|
      call_multimethod(nombre, *args)
    end
  end

  def multimethods
    @multimethods ||= Hash.new
  end

  def multimethod(nombre)
    multimethods[nombre]
  end
end

class Object
  def call_multimethod(nombre, *args)
    a_method = self.class.multimethod(nombre)

    return method_missing(nombre, *args) if a_method.nil?

    a_method.call(self, *args)
  end

  alias_method :__respond_to?, :respond_to?

  def respond_to?(name, include_all = false, types = nil)
    if types.nil?
      __respond_to?(name, include_all)
    else
      self.class.multimethod(name).matches_types? types
    end
  end
end
