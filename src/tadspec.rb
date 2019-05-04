module Matchers
  class Config
    def initialize(proc)
      @proc = proc
    end

    def call(un_objeto)
      @proc.call(un_objeto)
    end
  end

  def respond_to_missing?(symbol, include_ancestors = false)
    super || symbol.to_s.start_with?("ser_")
  end

  def no(un_matcher)
    proc { |obj| !un_matcher.call(obj) }
  end

  def method_missing(symbol, *args, &block)
    unless respond_to_missing? symbol
      super
    end

    metodo_a_mandar =
        ((symbol.to_s.gsub "ser_", "") + "?").to_sym

    #devuelvo un matcher
    proc { |obj|
      obj.respond_to?(metodo_a_mandar) && obj.send(metodo_a_mandar)
    }
  end

  def igual(a_algo)
    Config.new(proc { |unObjeto| unObjeto == a_algo })
  end

  def mayor_a(a_algo)
    Config.new(proc { |unObjeto| unObjeto > a_algo })
  end

  def uno_de_estos(objetos)
    Config.new(proc { |unObjeto| objetos.include? unObjeto })
  end

  def ser(config)
    if config.is_a? Config
      config
    else
      igual(config)
    end
  end

  def menor_a(algo)
    Config.new(proc { |unObjeto| unObjeto < algo })
  end
end

class TadspecAssertionError < StandardError

end

class Object
  def deberia(matcher)
    unless matcher.call(self)
      raise TadspecAssertionError
    end
  end
end

# module MockRegistry
#
#   def self.registry
#     @
#   end
#
#   def self.register_mock(clazz, symbol)
#
#   end
# end

class Module

  def original_methods
    @original_methods ||= Hash.new
    @original_methods
  end

  def clear_mocks
    original_methods.each do |key, value|
      self.define_method key, value
    end
  end

  def mockear(symbol, &block)
    metodo_anterior = self.instance_method symbol

    original_methods[symbol] = metodo_anterior

    self.define_method symbol, block
  end
end