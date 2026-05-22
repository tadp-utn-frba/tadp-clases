class PrototypedObject
  def initialize
    @__properties = {}
    @__prototype

    @__prototype_module = Module.new
    self.extend @__prototype_module
  end

  def set_property(key, value)
    @__properties[key] = value
    set_method(key, proc{|| get_property(key)})
  end

  def get_property(key)
    @__properties[key]
  end

  def set_method(method_sym, block)
    @__prototype_module.define_method(
      method_sym, &block)
  end

  def clone
    shallow_copy = PrototypedObject.new
    shallow_copy.
      instance_variable_set(
        :@__properties, self.
        instance_variable_get(:@__properties).clone)
    shallow_copy.set_prototype(self)
    shallow_copy
  end

  def set_prototype(prototype)
    @__prototype_module.
      include(
        prototype.
          instance_variable_get(:@__prototype_module))
  end
end



