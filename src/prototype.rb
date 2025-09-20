class PrototypedObject
  def initialize
    @__methods__ = {}
  end
  def set_property(property_name, property_value)
    instance_variable_set(
      "@#{property_name}", property_value
    )

    set_method(property_name, proc do
      get_property(property_name)
    end)
  end

  def get_property(property_name)
    instance_variable_get("@#{property_name}")
  end

  def set_method(method_name, a_proc)
    @__methods__[method_name] = a_proc
  end

  def set_prototypes(prototypes)
    @__prototypes__ = prototypes
  end

  private def method_missing(symbol, *args)
    method_proc = lookup_method(symbol)
    return super unless method_proc

    instance_exec(*args, &method_proc)
  end

  def lookup_method(symbol)
    prototypes = (@__prototypes__ || [])
    prototypes.reduce(@__methods__[symbol]) do |a_proc, prototype|
      a_proc || prototype.lookup_method(symbol)
    end
  end
end
