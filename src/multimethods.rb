class PartialBlock
  attr_accessor :block, :types

  def initialize types, &block
    self.types = types
    self.block = block
  end

  def distancia_types(*args)
    args.map.with_index { |argumento, index| argumento.class.ancestors
                                                 .index(self.types[index])*(index+1) }.reduce :+
  end

  def matches(*values)
    unless values.length == types.length
      return false
    end

    values.zip(types).all? { |value, type|
      if type.is_a? Array
        #duck typing
        type.all? {|method| value.respond_to? method}
      else
        value.is_a? type
      end }
  end
end

def matches_types(types)
  unless self.types.length == types.length
    return false
  end

  self.types.zip(types).all? { |ancestor_type, type| type.ancestors.include?
  ancestor_type }
end

end

class Module

  attr_accessor :multimethods

  def partial_def sym, types, &block
    self.multimethods[sym] ||= []
    self.multimethods[sym] << (PartialBlock.new types, &block)

    current_module = self

    self.send :define_method, sym do |*args|
      partial_block = current_module.search_multimethods sym, args
      self.instance_exec *args, &partial_block.block
    end


  end

  def search_multimethods sym, args
    matched_methods = self.multimethods[sym].
        select { |method| method.matches args }


    raise NoMethodError, 'No se encontro metodo con la cantidad de argumentos pasados' if matched_methods.empty?

    matched_methods.min_by { |p_block| p_block.distancia_types args }
  end

  def multimethods
    @multimethods ||= Hash.new
  end


  def defines_multimethod?(sym, types)
    self.multimethods[sym].any? { |m| m.matches_types types }
  end

end


class Object
  alias_method :__respond_to?, :respond_to?

  def respond_to?(sym, private=false, types=nil)
    if types.nil?
      return self.__respond_to? sym, private
    end

    self.singleton_class.defines_multimethod? sym, types
  end
end




