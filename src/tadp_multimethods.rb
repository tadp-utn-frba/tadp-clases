class PartialBlock
  attr_accessor :tipos, :bloque

  def initialize(_tipos , &block)
    self.tipos = _tipos
    self.bloque = block
  end

  def matches?(*varargs)
    return false unless varargs.length == tipos.length
    return varargs.zip(self.tipos).all? do |x|
      instancia = x[0]
          clase = x[1]
      instancia.is_a? clase
    end
  end

  def call(*args)
    raise ArgumentError unless self.matches?(*args)
    self.bloque.call(*args)
  end


  def distancia(*args)
    args.map.with_index  do |arg,index|
      arg.class.ancestors.index(self.tipos[index])*(index + 1)
    end
    .reduce :+
  end

end

class MultiMethod
  attr_accessor :partial_blocks,:nombre

  def initialize(nombre)
    self.nombre = nombre
    self.partial_blocks = []
  end

  def add_partial_block(partial_block)
    partial_blocks << partial_block
  end

  def matches_to_args?(args)
    self.partial_blocks.any? {|partial| partial.tipos == args}
  end

  def call(*args)
    posible_bloque = self.partial_blocks
                         .select {|partial| partial.matches? *args}
                         .sort_by { |partial| partial.distancia *args }.first()

    return posible_bloque.call(*args) unless posible_bloque.nil?

    raise NoMethodError.new('No existe un multimethod para este metodo',self.nombre,*args)
  end

end

class Module
  attr_accessor :multimethods

  def multimethods
    @multimethods ||= Hash.new { |hash,key| hash[key] = MultiMethod.new(key)}
  end

  def partial_def (nombre ,tipos, &block)
    self.multimethods[nombre].add_partial_block(PartialBlock.new(tipos,&block))

    _self = self
    self.send :define_method , nombre do |*args|
      _self.multimethods[nombre].call(*args)
    end
  end

  def multimethod(nombre)
    multimethods[nombre]
  end
end

class Object
  def respond_to?(nombre,privados=false,tipos=[])
    return super(nombre,privados) if tipos.empty?


    self.class.multimethod(nombre).matches_to_args?(tipos)
  end
end







