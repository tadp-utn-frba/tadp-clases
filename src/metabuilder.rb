require_relative '../src/builder'

class Metabuilder
  attr_accessor :klass, :properties, :validations

  def validations
    @validations ||= []
  end

  def properties
    @properties ||= []
  end

  def set_class(klass)
    self.klass = klass
  end

  def with_properties(*properties)
    self.properties += properties
  end

  def validate(&block)
    self.validations << block
  end

  def build
    builder = Builder.new
    builder.klass = self.klass
    builder.validations = self.validations
    builder.initialize_properties(self.properties)
    builder
  end

  def create_class(sym, &block)
    if Object.const_defined? sym
      klass = Object.const_get sym
    else
      klass = Class.new
      Object.const_set(sym, klass)
    end

    klass.class_eval &block
    self.set_class klass

  end

end




