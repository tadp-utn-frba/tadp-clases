class Object
  def clone_object
    PrototypedObject.new self
  end
end

class PrototypedObject
  attr_accessor :prototype

  def initialize(prototype= Object.new)
    self.prototype = prototype
  end

  def set_property(sym, value)
    self.singleton_class.send :attr_accessor, sym
    self.send "#{sym}=", value
  end

  def set_method(sym, block)
    self.define_singleton_method sym, block
  end

  def respond_to?(sym)
    super or self.prototype.respond_to? sym
  end

  def method(sym)
    begin
      super
    rescue NameError
      self.prototype.method sym
    end
  end

  def method_missing(sym, *args)
    super unless respond_to? sym
    method = self.prototype.method(sym).unbind
    method.bind(self).call *args
  end
end
