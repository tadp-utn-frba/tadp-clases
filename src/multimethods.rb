class PartialBlock
  attr_accessor :block

  def initialize(types, &block)
    @types = types
    @block = block
  end

  def matches(*args)
    matches_types(*args.map { |arg|
      begin
        arg.singleton_class
      rescue
        # singleton_class no se puede usar para números (1.singleton_class => error)
        arg.class
      end
    })
  end

  def matches_types(*types)
    if (@types.size == types.size)
      types.zip(@types).all? do |arg_type, type|
        if (type.is_a?(Array))
          type.all? { |sym|
            arg_type.instance_methods.include?(sym)
          }
        else
          arg_type <= type
        end
      end
    else
      false
    end
  end

  def bind(instance)
    # BoundPartialBlock.new(self, instance)
  end

  def call_with(instance, *args)
    validate(*args)
    instance.instance_exec(*args, &@block)
  end

  def validate(*args)
    if (!matches(*args))
      raise ArgumentError.new 'No existe un multimethod para este metodo'
    end
  end

  def call(*args)
    validate(*args)
    @block.call(*args)
  end
end

class Object
  def respond_to?(sym, all = false, types = [])
    if (types.empty?)
      super(sym, all)
    else
      multimethod = self.class.multimethod(sym)
      if (multimethod)
        multimethod.matches_types(*types)
      else
        false
      end
    end
  end
end

class Module
  def partial_def(sym, types, &block)
    multimethod = multimethod(sym) || Multimethod.new(sym)
    multimethod.add(PartialBlock.new(types, &block))
    @multimethods[sym] = multimethod
    define_method(sym) do |*args|
      pBlock = multimethod.find_matching(*args)
      if (pBlock)
        # instance_exec(*args, &pBlock.block)
        pBlock.call_with(self, *args)
      else
        raise NoMethodError.new 'No existe un multimethod para este metodo'
      end
    end
  end

  def multimethods
    @multimethods ||= {}
  end

  def multimethod(sym)
    multimethods[sym]
  end
end

class Multimethod
  attr_accessor :sym, :pBlocks

  def initialize(sym)
    @sym = sym
    @pBlocks = []
  end

  def add(pBlock)
    @pBlocks.push pBlock
  end

  def find_matching(*args)
    @pBlocks.find do |block|
      block.matches(*args)
    end
  end

  def matches_types(*types)
    @pBlocks.any? do |block|
      block.matches_types(*types)
    end
  end
end

# class Object
#   partial_def :algo, [Array] do
#     type.all? { |sym|
#       arg_type.instance_methods.include?(sym)
#     }
#   end
#
#   partial_def :algo, [Module] do
#     arg_type <= type
#   end
# end