class Builder
  attr_accessor :klass, :properties, :validations

  def initialize
    self.properties = {}
  end

  def build
    instance = self.klass.new
    self.properties.each do |sym, value|
      instance.send("#{sym}=".to_sym, value)
    end
    raise ValidationError unless self.validate?(instance)
    instance
  end

  def validate?(instance)
    self.validations.all? do |validation|
      instance.instance_eval &validation
    end
  end

  def set_property(sym, value)
    self.properties[sym] = value
  end

  def initialize_properties(properties)
    properties.each do |property|
      self.set_property(property, nil)
    end
  end

  def method_missing(symbol, *args)
    #:raza=
    property = symbol.to_s[0..-2].to_sym

    raise NoMethodError unless self.properties.has_key?(property)
    self.set_property(property, args[0])

  end

end


class ValidationError < StandardError
end





