module Prototyped
  attr_reader :proto_mixin

  def set_property(property, value)
    @proto_mixin.send(:attr_accessor, property)
    send("#{property}=".to_sym, value)
  end

  def set_method(symbol, proc)
    @proto_mixin.send(:define_method, symbol, &proc)
  end

  def initialize
    @proto_mixin = Module.new
    refresh_singleton_class
  end

  def set_prototype(prototype)
    @proto_mixin.send(:include, prototype.proto_mixin)
    refresh_singleton_class
  end

  def method_missing(symbol, *args, &block)
    if symbol.to_s.end_with?("=") && args.length == 1
      final_symbol = symbol.to_s.sub("=", "").to_sym
      if args[0].is_a? Proc
        set_method final_symbol, args[0]
      else
        set_property final_symbol, args[0]
      end
    else
      super
    end
  end

  private

  def refresh_singleton_class
    self.singleton_class.send(:include, @proto_mixin)
  end
end