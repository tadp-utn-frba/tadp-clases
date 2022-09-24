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