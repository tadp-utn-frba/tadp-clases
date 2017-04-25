class Proc
  def call_in(object, *args)
    object.instance_exec(*args, &self)
  end
end

class Array
  def include_all?(elements)
    elements.all? { |element| include?(element) }
  end
end

class PartialBlock
  attr_reader :types, :block

  def initialize(types, &block)
    @types = types
    @block = block
  end

  def matches?(*values)
    matches_types?(values.map(&:class))
  end

  def matches_types?(parameter_types)
    return false unless types.length == parameter_types.length

    types.zip(parameter_types).all? do |type, parameter_type|
      case type
        when Module; parameter_type <= type
        when Array; parameter_type.instance_methods.include_all?(type)
      end
    end
  end

  def call(*values)
    validate_call(*values)

    block.call(*values)
  end

  def call_with(receiver, *values)
    validate_call(*values)

    block.call_in(receiver, *values)
  end

  private

  def validate_call(*values)
    raise ArgumentError unless matches?(*values)
  end
end

class Module
  def partial_def(name, types, &block)
    multimethods[name].add_partial_block(PartialBlock.new(types, &block))

    multimethods = self.multimethods

    define_method(name) do |*args|
      multimethods[name].call(self, *args)
    end
  end

  def multimethods
    @multimethods ||= Hash.new { |hash, key| hash[key] = Multimethod.new }
  end
end

class Multimethod
  attr_reader :partial_blocks
  def initialize(partial_blocks = [])
    @partial_blocks = partial_blocks
  end

  def add_partial_block(partial_block)
    partial_blocks << partial_block
  end

  def call(receiver, *args)
    partial_block = partial_blocks.find { |partial_block| partial_block.matches?(*args) }

    raise NoMethodError.new('No existe un multimethod para este metodo') if partial_block.nil?

    partial_block.call_with(receiver, *args)
  end

  def matches?(types)
    partial_blocks.any? { |partial_block| partial_block.matches_types?(types) }
  end
end

class Object
  def multimethods
    self.class.multimethods
  end

  def respond_to?(name, include_all = true, types = [])
    return false unless super(name, include_all)
    types == [] || respond_to_multimethod?(name, include_all, types)
  end

  def respond_to_multimethod?(name, include_all, types)
    multimethods.key?(name) && multimethods[name].matches?(types)
  end
end

# class PartialBlock
#   attr_accessor :block, :types
#
#   def initialize types, &block
#     self.types = types
#     self.block = block
#   end
#
#   def distancia_types(*args)
#     args.map.with_index { |argumento, index| argumento.class.ancestors
#                                                  .index(self.types[index])*(index+1) }.reduce :+
#   end
#
#   def matches(*values)
#     unless values.length == types.length
#       return false
#     end
#
#     values.zip(types).all? { |value, type|
#       if type.is_a? Array
#         #duck typing
#         type.all? {|method| value.respond_to? method}
#       else
#         value.is_a? type
#       end }
#   end
#
#   def matches_types??(types)
#     unless self.types.length == types.length
#       return false
#     end
#
#     self.types.zip(types).all? { |ancestor_type, type| type.ancestors.include?
#     ancestor_type }
#   end
# end
#
# class Module
#
#   attr_accessor :multimethods
#
#   def partial_def sym, types, &block
#     self.multimethods[sym] ||= []
#     self.multimethods[sym] << (PartialBlock.new types, &block)
#
#     current_module = self
#
#     self.send :define_method, sym do |*args|
#       partial_block = current_module.search_multimethods sym, args
#       self.instance_exec *args, &partial_block.block
#     end
#
#
#   end
#
#   def search_multimethods sym, args
#     matched_methods = self.multimethods[sym].
#         select { |method| method.matches args }
#
#
#     raise NoMethodError, 'No se encontro metodo con la cantidad de argumentos pasados' if matched_methods.empty?
#
#     matched_methods.min_by { |p_block| p_block.distancia_types args }
#   end
#
#   def multimethods
#     @multimethods ||= Hash.new
#   end
#
#
#   def defines_multimethod?(sym, types)
#     self.multimethods[sym].any? { |m| m.matches_types?? types }
#   end
#
# end
#
#
# class Object
#   alias_method :__respond_to?, :respond_to?
#
#   def respond_to?(sym, private=false, types=nil)
#     if types.nil?
#       return self.__respond_to? sym, private
#     end
#
#     self.singleton_class.defines_multimethod? sym, types
#   end
# end
#
#
#
#
