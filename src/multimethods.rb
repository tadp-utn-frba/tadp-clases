class PartialBlock
  def initialize(tipos, &bloque)
    @tipos = tipos
    @bloque = bloque
    if tipos.length != bloque.parameters.length
      raise ArgumentError
    end
  end

  def distance_to(*parametros)
    parametros
      .zip(@tipos, (1..parametros.length))
      .sum do |parametro, tipo, indice|
        indice * distance_for(parametro, tipo)
      end
  end

  def distance_for(parametro, tipo)
    parametro.class.ancestors.index(tipo)
  end

  def matches?(*parametros)
    parametros.length == @tipos.length and
      parametros.zip(@tipos).all? do |parametro, tipo|
        parametro.is_a? tipo
      end
  end

  def matches_types?(*tipos_esperados)
    tipos_esperados.length == @tipos.length and
      tipos_esperados.zip(@tipos).all? do |tipo_esperado, tipo|
        tipo_esperado <= tipo
      end
  end

  def validate_parametros(*parametros)
    raise ArgumentError unless matches?(*parametros)
  end

  def call(*parametros)
    validate_parametros(*parametros)
    @bloque.call(*parametros)
  end
end

class Module
  attr_reader :multimethod

  def partial_def(nombre, tipos, &bloque)
    @multimethod ||= MultiMethod.new(nombre)

    @multimethod.add_definition(tipos, &bloque)

    define_method(nombre) do |*parametros|
      self.class.multimethod.call(*parametros)
    end
  end
end

class MultiMethod
  def initialize(nombre)
    @partial_blocks = []
    @nombre = nombre
  end

  def add_definition(tipos, &bloque)
    partial_block = PartialBlock.new(tipos, &bloque)
    @partial_blocks.push(partial_block)
  end


  def call(*parametros)
    partial_block_for(*parametros).call(*parametros)
  end

  def partial_block_for(*parametros)
    possible_partial_blocks = @partial_blocks.select { |partial_block| partial_block.matches?(*parametros) }
    raise ArgumentError if (possible_partial_blocks.empty?)
    possible_partial_blocks.min_by { |partial_block| partial_block.distance_to(*parametros) }
  end

  def matches?(tipos)
    @partial_blocks.any? do |partial_block|
      partial_block.matches_types?(*tipos)
    end
  end
end

class Object
  # alternativas para no perder el respond_to?:
  # alias_method :old_respond_to?, :respond_to?
  # old_respond_to = method(:respond_to?)

  def respond_to?(name, include_all=false, tipos=nil)
    if tipos == nil
      super(name, include_all)
    else
      self.class.multimethod.matches?(tipos)
    end
  end
end
