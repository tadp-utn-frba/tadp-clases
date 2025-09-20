class PartialBlock
  def initialize(types, &block)
    @types = types
    @block = block
    raise ArgumentError.new if types.size != block.arity
  end

  def matches?(*parameters)
    parameters.size == @types.size &&
      parameters.zip(@types).all? do |parameter, type|
        matches_type?(parameter, type)
      end
  end

  def matches_type?(parameter, type)
    if type.is_a? Module
      parameter.is_a? type
    else
      type.all? { |method_name| paramter.respond_to?(method_name) }
    end
  end


  def call(*parameters)
    raise ArgumentError.new unless matches?(*parameters)

    @block.call(*parameters)
  end

  def call_in_context(context, *parameters)
    context.instance_exec(*parameters, &@block)
  end
end

# partial_def :concat, [String, String] do |s1,s2|
#   s1 + s2
# end
module WithPartialBlocks
  attr_reader :multimethods
  def partial_def(method_name, types, &block)
    @multimethods ||= {}
    @multimethods[method_name] ||= []
    @multimethods[method_name].push(PartialBlock.new(types, &block))

    define_method(method_name) do |*parameters|
      partial_blocks = self.class.multimethods[method_name]
      partial_block = partial_blocks.find(proc { raise NoMethodError.new }) { |partial_block| partial_block.matches?(*parameters) }
      partial_block.call_in_context(self, *parameters)
    end
  end
end

class Module
  include WithPartialBlocks
end
