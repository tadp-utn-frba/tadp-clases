class PartialBlock

  attr_reader :tipos, :bloque

  def initialize(lista_tipos, &block)
    @tipos = lista_tipos
    @bloque = block

    raise ArgumentError unless lista_tipos.length == block.parameters.length
  end

  def matches?(*argumentos)
    argumentos.length == tipos.length &&
      tipos.zip(argumentos).all? do |tipo, argumento|
        if tipo.is_a?(Array)
          # [[:nombre, :direccion], Lugar.new]
          tipo.all? do |mensaje|
            argumento.respond_to?(mensaje)
          end
        else
          # [[String, 'a']]
          argumento.is_a? tipo
        end

      end
  end

  def matches_types?(*tipos_esperados)
    tipos_esperados.length == tipos.length &&
      tipos.zip(tipos_esperados).all? do |tipo, tipo_esperado|
        if tipo.is_a?(Array)
          tipo.all? do |mensaje|
            tipo_esperado.instance_methods.include(mensaje)
          end
        else
          tipo_esperado < tipo
        end
      end
  end

  def call(*argumentos)
    raise ArgumentError.new "El bloque requiere #{tipos.length} argumentos" unless matches?(*argumentos)
    bloque.call(*argumentos)
  end

  def evaluate_in(instancia, *argumentos)
    instancia.instance_exec(*argumentos, &bloque)
  end

  def distance_to(*parametros)
    parametros
      .zip(@tipos, (1..parametros.length))
      .sum do |parametro, tipo, indice|
      if tipo.is_a?(Array)
        0.5
      else
        indice * distance_for(parametro, tipo)
      end
    end
  end

  def distance_for(parametro, tipo)
    parametro.class.ancestors.index(tipo)
  end
end

class Module
  def partial_def(method_name, tipos, &block)
    # agregando la definicion
    partial_blocks = partial_blocks(method_name)
    partial_blocks.append(PartialBlock.new(tipos, &block))

    define_method method_name do |*args|
      # tiempo de ejecucion
      # FIXME: reificar a multimethod y delegar.
      partial_block = partial_blocks.select do |partial_block|
        partial_block.matches?(*args)
      end.min_by do |partial_block|
        partial_block.distance_to(*args)
      end

      raise ArgumentError if partial_block.nil?


      partial_block.evaluate_in(self, *args)
    end
  end

  def multimethods
    (@partial_blocks || {}).keys
  end

  def multimethod(method_name)
    partial_blocks(method_name)
  end

  def partial_blocks(method_name)
    # lazy initialization
    @partial_blocks ||= {}
    @partial_blocks[method_name] ||= []
  end
end

class Object

  def respond_to?(sym, include_all=false, tipos=nil)
    return super(sym, include_all) if tipos.nil?

    self.class.multimethod(sym).any? do |partial_block|
      partial_block.matches_types?(*tipos)
    end
  end
end







































