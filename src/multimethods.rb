class PartialBlock
  def initialize(tipos, &bloque)
    @tipos = tipos
    @bloque = bloque
    if tipos.length != bloque.parameters.length
      raise ArgumentError
    end
  end

  def matches?(*parametros)
    parametros.length == @tipos.length and
      parametros.zip(@tipos).all? do |parametro, tipo|
        parametro.is_a? tipo
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
    @partial_blocks.find(proc { raise ArgumentError }) do |partial_block|
      partial_block.matches?(*parametros)
    end
  end
end