require_relative "../src/prototyped_object"

class PrototypedConstructor
  def initialize(prototype, super_constructor = nil, &block)
    @prototype = prototype
    @block = block
    @super_constructor = super_constructor
  end

  def new(*args)
    new_object = PrototypedObject.new
    new_object.set_prototype @prototype

    invoke_block(new_object, args)

    new_object
  end
  
  def invoke_block(new_object, args)
    unless @super_constructor.nil?
      args = @super_constructor.invoke_block(
          new_object, args
      )
    end

    new_object.instance_exec(*(args.take(@block.arity)),
                             &@block)

    args.drop(@block.arity)
  end

  def extended(&block)
    PrototypedConstructor
        .new(@prototype,
             self,
             &block)
    
    # {|*args|
    #   self.instance_exec(
    #       *(args.take(old_block.arity)), &old_block
    #   )
    #   self.instance_exec(*(args.drop(old_block.arity)), &block)
    # }
  end
end