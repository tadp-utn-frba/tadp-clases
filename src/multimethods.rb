class PartialBlock

  attr_accessor :lista_de_tipos, :bloque

  def initialize(lista_de_tipos, &block)
    self.lista_de_tipos = lista_de_tipos
    self.bloque = block
  end

  def matches?(*valores)
    return false if valores.size != self.lista_de_tipos.size

    lista_zipeada = self.lista_de_tipos.zip(valores)
    lista_zipeada.all? do |tipo, valor|
      valor.is_a? tipo
    end
  end

  def call(*valores)
    raise ArgumentError unless self.matches?(*valores)

    self.bloque.call *valores
  end

end

class Module

  def multimethods
    @multimethods ||= Hash.new
  end

  def multimethod(sym)
    multimethods[sym] ||= []
  end

  def partial_def(sym, lista_argumentos, &block)
    partial_block = PartialBlock.new lista_argumentos, &block
    multimethod(sym) << partial_block

    define_method sym do |*args|
      selected_partial_block = self.class.multimethods[sym].find do |pb|
        pb.matches? *args
      end
      raise ArgumentError unless selected_partial_block
      selected_partial_block.call(*args)
    end
  end
end

class Object
  def respond_to?(name, include_all = true, types = [])
    return false unless super(name, include_all)
    types == [] || respond_to_multimethod?(name, include_all, types)
  end

  def respond_to_multimethod?(name, include_all, types)
    self.class.multimethod(name).any? do |pb|
      pb.lista_de_tipos == types
    end
  end
end