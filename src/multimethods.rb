class PartialBlock
  def initialize(tipos, &bloque)
    @tipos = tipos
    @bloque = bloque
  end

  def matches?(*parametros)
    parametros.length == @tipos.length &&
        @tipos.zip(parametros).all? do |tipo, parametro|
          # parametro.is_a? tipo
          if tipo.is_a? Array
            tipo.all? { |method| parametro.respond_to?(method) }
          else
            parametro.is_a? tipo
          end
        end
  end

  def matches_types?(*tipos)
    tipos.length == @tipos.length &&
        @tipos.zip(tipos).all? do |tipo_del_partial, tipo_a_matchear|
          # tipo_a_matchear <= tipo_del_partial
          if tipo_del_partial.is_a?(Array) && tipo_a_matchear.is_a?(Array)
            tipo_a_matchear.all? { |method| tipo_del_partial.include?(method) }
          elsif tipo_del_partial.is_a?(Module) && tipo_a_matchear.is_a?(Module)
            tipo_a_matchear <= tipo_del_partial
          else
            false
          end
        end
  end

  def call(*parametros)
    validate_parameters(*parametros)

    @bloque.call(*parametros)
  end

  def eval_on(instancia, *parametros)
    validate_parameters(*parametros)

    instancia.instance_exec(*parametros, &@bloque)
  end

  private

  def validate_parameters(*parametros)
    raise ArgumentError unless matches?(*parametros)
  end
end

class Module
  def multimethod(nombre, *params)
    partial_block = multimethods[nombre].find { |pb| pb.matches?(*params) }
    if partial_block.nil?
      if ancestors[1].nil?
        nil
      else
        ancestors[1].multimethod(nombre, *params)
      end
    else
      partial_block
    end
  end

  def partial_def(nombre, tipos, &bloque)
    partial_block = PartialBlock.new(tipos, &bloque)
    multimethods[nombre].push(partial_block)
    mi_clase = self

    define_method(nombre) do |*parametros|
      partial = self.class.multimethod(nombre, *parametros)
      if partial.nil?
        method_missing(nombre, *parametros)
      else
        partial.eval_on(self, *parametros)
      end
    end
  end

  def multimethods
    @multimethods ||= Hash.new do |dict, key|
      dict[key] = []
    end
  end
end

class Object
  def respond_to?(name, include_private = false, types = nil)
    if types.nil?
      super(name, include_private)
    else
      self.class.partial_method_defined?(name, types)
    end
  end
end

class Module
  def partial_method_defined?(name, types)
    multimethods[name].any? { |partial_block| partial_block.matches_types?(*types) }
  end
end